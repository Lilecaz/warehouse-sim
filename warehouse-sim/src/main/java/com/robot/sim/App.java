package com.robot.sim;

import com.robot.sim.engine.RobotAgent;
import com.robot.sim.gui.GridPanel;
import com.robot.sim.model.Grid;
import com.robot.sim.model.Node;
import com.robot.sim.model.Robot;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        // 1. Initialisation
        int width = 60;
        int height = 40;
        Grid grid = new Grid(width, height);
        List<Robot> robots = new ArrayList<>();
        List<RobotAgent> agents = new ArrayList<>();

        // 2. Création des Robots et Agents
        Random rand = new Random();
        for (int i = 0; i < 20; i++) { // On met 20 robots !
            // Trouver une case vide pour spawner
            Node startNode = null;
            while (startNode == null || !startNode.isWalkable) {
                startNode = grid.getNode(rand.nextInt(width), rand.nextInt(height));
            }

            Robot robot = new Robot(i, startNode);
            robots.add(robot);

            RobotAgent agent = new RobotAgent(robot, grid);
            agents.add(agent);
            
            // Démarrage du thread (Cerveau du robot)
            new Thread(agent).start();
        }

        // 3. Interface Graphique
        JFrame frame = new JFrame("Simulation Multi-Agents A*");
        GridPanel panel = new GridPanel(grid, robots);
        
        frame.add(panel);
        frame.setSize(panel.getPreferredWidth() + 20, panel.getPreferredHeight() + 40);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 4. Boucle d'affichage (Main Loop)
        while (true) {
            panel.repaint();
            try {
                Thread.sleep(30); // ~30 FPS pour l'affichage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}