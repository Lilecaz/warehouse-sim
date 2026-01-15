package com.robot.sim.engine;

import com.robot.sim.algo.AStar;
import com.robot.sim.model.Grid;
import com.robot.sim.model.Node;
import com.robot.sim.model.Robot;

import java.util.List;
import java.util.Random;

public class RobotAgent implements Runnable {
    private Robot robot;
    private Grid grid;
    private boolean running = true;
    private Random random = new Random();
    
    private int patience = 0;
    private final int MAX_PATIENCE = 5; 

    public RobotAgent(Robot robot, Grid grid) {
        this.robot = robot;
        this.grid = grid;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // 1. Définir une cible si nécessaire
                if (robot.getTarget() == null || robot.getPosition().equals(robot.getTarget())) {
                    pickNewTarget();
                }

                // 2. Calcul initial du chemin (si vide)
                if (robot.getCurrentPath() == null || robot.getCurrentPath().isEmpty()) {
                    List<Node> path = AStar.findPath(robot.getPosition(), robot.getTarget(), grid.getNodes());
                    if (path != null) {
                        robot.setCurrentPath(path);
                    } else {
                        // Si cible inaccessible (mur complet), on en change
                        pickNewTarget();
                    }
                }

                // 3. Déplacement
                moveNextStep();

                // 4. Vitesse variable pour éviter que tous bougent exactement en même temps
                Thread.sleep(200 + random.nextInt(100));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void moveNextStep() {
        List<Node> path = robot.getCurrentPath();
        
        if (path != null && !path.isEmpty()) {
            Node nextNode = path.get(0);
            Node currentNode = robot.getPosition();

            synchronized (nextNode) {
                if (!nextNode.isOccupied()) {
                    // --- CAS NORMAL : La voie est libre ---
                    executeMove(currentNode, nextNode);
                    path.remove(0);
                    patience = 0; // Tout va bien, on reset la patience
                } else {
                    // --- CAS BLOQUÉ : Embouteillage ---
                    patience++;
                    
                    // NIVEAU 1 : Recalcul intelligent (Coût dynamique)
                    if (patience > 5 && patience <= 15) {
                         List<Node> newPath = AStar.findPath(currentNode, robot.getTarget(), grid.getNodes());
                         if (newPath != null) robot.setCurrentPath(newPath);
                    }
                    
                    // NIVEAU 2 : Mouvement Aléatoire pour débloquer
                    else if (patience > 15 && patience <= 30) {
                        tryRandomMove();
                    }

                    // NIVEAU 3 : TÉLÉPORTATION D'URGENCE (Respawn)
                    // Si après ~7 secondes on est toujours coincé, on saute sur une case libre à côté
                    else if (patience > 30) {
                        respawnNearby(); 
                        patience = 0;
                    }
                }
            }
        }
    }

    private void executeMove(Node from, Node to) {
        from.setOccupant(null); // On libère l'ancienne case
        to.setOccupant(robot);  // On occupe la nouvelle
        robot.setPosition(to);
    }

    private void pickNewTarget() {
        Node target = null;
        while (target == null || !target.isWalkable) {
            int x = random.nextInt(grid.getWidth());
            int y = random.nextInt(grid.getHeight());
            target = grid.getNode(x, y);
        }
        robot.setTarget(target);
        robot.setCurrentPath(null);
    }

    private void tryRandomMove() {
        int x = robot.getPosition().x;
        int y = robot.getPosition().y;
        
        // Directions : Haut, Bas, Droite, Gauche
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        
        // Mélanger les directions pour ne pas toujours aller du même côté
        for (int i = 0; i < dirs.length; i++) {
            int randIndex = random.nextInt(dirs.length);
            int[] temp = dirs[i];
            dirs[i] = dirs[randIndex];
            dirs[randIndex] = temp;
        }

        for (int[] dir : dirs) {
            Node neighbor = grid.getNode(x + dir[0], y + dir[1]);
            
            if (neighbor != null && neighbor.isWalkable) {
                synchronized(neighbor) {
                    if (!neighbor.isOccupied()) {
                        executeMove(robot.getPosition(), neighbor);
                        robot.setCurrentPath(null); // On force le recalcul au prochain tour
                        patience = 0;
                        return;
                    }
                }
            }
        }
    }

    private void respawnNearby() {
        // Cherche une case libre dans un carré de 5x5 autour du robot
        int cx = robot.getPosition().x;
        int cy = robot.getPosition().y;
        
        for (int x = cx - 2; x <= cx + 2; x++) {
            for (int y = cy - 2; y <= cy + 2; y++) {
                Node n = grid.getNode(x, y);
                if (n != null && n.isWalkable) {
                    synchronized(n) {
                         if (!n.isOccupied()) {
                             executeMove(robot.getPosition(), n);
                             robot.setCurrentPath(null);
                             return;
                         }
                    }
                }
            }
        }
    }

    public void stop() { this.running = false; }
}