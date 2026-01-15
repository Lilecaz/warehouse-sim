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

    public RobotAgent(Robot robot, Grid grid) {
        this.robot = robot;
        this.grid = grid;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // 1. Si pas de cible ou cible atteinte, on en choisit une nouvelle
                if (robot.getTarget() == null || robot.getPosition().equals(robot.getTarget())) {
                    pickNewTarget();
                }

                // 2. Calcul du chemin (Si pas de chemin ou chemin vide)
                if (robot.getCurrentPath() == null || robot.getCurrentPath().isEmpty()) {
                    List<Node> path = AStar.findPath(robot.getPosition(), robot.getTarget(), grid.getNodes());
                    robot.setCurrentPath(path);
                }

                // 3. Déplacement
                moveNextStep();

                // 4. Pause (Vitesse du robot)
                Thread.sleep(200 + random.nextInt(100)); // Entre 200ms et 300ms par case

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void pickNewTarget() {
        // Choisir une case marchable au hasard
        Node target = null;
        while (target == null || !target.isWalkable) {
            int x = random.nextInt(grid.getWidth());
            int y = random.nextInt(grid.getHeight());
            target = grid.getNode(x, y);
        }
        robot.setTarget(target);
        // On reset le path pour forcer le recalcul
        robot.setCurrentPath(null);
    }

    private void moveNextStep() {
        List<Node> path = robot.getCurrentPath();
        
        if (path != null && !path.isEmpty()) {
            Node nextNode = path.get(0);
            Node currentNode = robot.getPosition();
            synchronized (nextNode) {
                if (!nextNode.isOccupied()) {
                    
                    currentNode.setOccupant(null);
                    
                    nextNode.setOccupant(robot);
                    robot.setPosition(nextNode);
                    
                    path.remove(0);
                } else {
                }
            }
        }
    }

    public void stop() { this.running = false; }
}