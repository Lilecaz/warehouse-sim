package com.robot.sim;

import com.robot.sim.engine.RobotAgent;
import com.robot.sim.gui.GridPanel;
import com.robot.sim.model.Grid;
import com.robot.sim.model.Node;
import com.robot.sim.model.Robot;
import com.robot.sim.algo.AABB;
import com.robot.sim.algo.Quadtree;
import java.awt.Color;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {    
    public static void main(String[] args) {
        int width = 60;
        int height = 40;
        Grid grid = new Grid(width, height);
        List<Robot> robots = new ArrayList<>();
        List<RobotAgent> agents = new ArrayList<>();

        Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            Node startNode = null;
            while (startNode == null || !startNode.isWalkable) {
                startNode = grid.getNode(rand.nextInt(width), rand.nextInt(height));
            }

            Robot robot = new Robot(i, startNode);
            startNode.setOccupant(robot);
            robots.add(robot);

            RobotAgent agent = new RobotAgent(robot, grid);
            agents.add(agent);
            
            new Thread(agent).start();
        }

        JFrame frame = new JFrame("Simulation Multi-Agents A*");
        GridPanel panel = new GridPanel(grid, robots);
        
        frame.add(panel);
        frame.setSize(panel.getPreferredWidth() + 20, panel.getPreferredHeight() + 40);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        while (true) {
            AABB boundary = new AABB(width / 2.0, height / 2.0, Math.max(width, height) / 2.0);
            Quadtree qt = new Quadtree(boundary, 4); 

            for (Robot r : robots) {
                qt.insert(r);
            }

            for (Robot r : robots) {
                AABB range = new AABB(r.getPosition().x, r.getPosition().y, 3);
                List<Robot> neighbors = qt.query(range, null);
                
                if (neighbors.size() > 3) {
                    r.setColor(Color.RED); 
                } else {
                    r.setColor(Color.GREEN);
                }
            }

            panel.repaint();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}