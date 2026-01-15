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
import javax.swing.JOptionPane; 

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {

    public static void main(String[] args) {
        int maxRobotsLimit = 100; 
        int numRobots = 20;

        String input = JOptionPane.showInputDialog(null, 
            "Combien de robots voulez-vous ? (Max " + maxRobotsLimit + ")", 
            "Configuration", 
            JOptionPane.QUESTION_MESSAGE);

        try {
            if (input != null) {
                numRobots = Integer.parseInt(input);
                if (numRobots < 1) numRobots = 1;
                if (numRobots > maxRobotsLimit) numRobots = maxRobotsLimit;
            }
        } catch (NumberFormatException e) {
            System.err.println("Entrée invalide, utilisation de la valeur par défaut : " + numRobots);
        }

        int areaNeeded = numRobots * 30; 
        int calculatedWidth = (int) Math.sqrt(areaNeeded * 1.5);
        int calculatedHeight = (int) Math.sqrt(areaNeeded / 1.5);

        int width = Math.max(40, Math.min(200, calculatedWidth));
        int height = Math.max(30, Math.min(200, calculatedHeight));

        System.out.println("Grille générée : " + width + "x" + height);

        Grid grid = new Grid(width, height);
        List<Robot> robots = new ArrayList<>();
        List<RobotAgent> agents = new ArrayList<>();

        Random rand = new Random();

        for (int i = 0; i < numRobots; i++) {
            Node startNode = null;
            
            int attempts = 0;
            while (startNode == null || !startNode.isWalkable || (startNode.isOccupied() && attempts < 100)) {
                startNode = grid.getNode(rand.nextInt(width), rand.nextInt(height));
                attempts++;
            }

            Robot robot = new Robot(i, startNode);
            startNode.setOccupant(robot); 
            robots.add(robot);

            RobotAgent agent = new RobotAgent(robot, grid);
            agents.add(agent);
            
            new Thread(agent).start();
        }

        JFrame frame = new JFrame("Simu Multi-Agents (" + numRobots + " robots)");
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
                
                // Si plus de 3 voisins -> ROUGE
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