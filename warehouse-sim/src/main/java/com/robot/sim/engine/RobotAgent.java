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
                if (robot.getTarget() == null || robot.getPosition().equals(robot.getTarget())) {
                    pickNewTarget();
                }

                if (robot.getCurrentPath() == null || robot.getCurrentPath().isEmpty()) {
                    List<Node> path = AStar.findPath(robot.getPosition(), robot.getTarget(), grid.getNodes(), false);
                    
                    if (path == null) {
                         pickNewTarget();
                    } else {
                         robot.setCurrentPath(path);
                    }
                }

                moveNextStep();

                Thread.sleep(200 + random.nextInt(100)); 

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
                    currentNode.setOccupant(null);
                    nextNode.setOccupant(robot);
                    robot.setPosition(nextNode);
                    path.remove(0);
                    
                    patience = 0;
                } else {
                    patience++;
                    
                    if (patience > MAX_PATIENCE) {
                        System.out.println("Robot " + robot.getId() + " est coincé -> Recalcul !");
                        
                        List<Node> newPath = AStar.findPath(currentNode, robot.getTarget(), grid.getNodes(), true);
                        
                        if (newPath != null && !newPath.isEmpty()) {
                            robot.setCurrentPath(newPath);
                            patience = 0; // On reset la patience
                        } else {
                            pickNewTarget();
                        }
                    }
                }
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

    public void stop() { this.running = false; }
}