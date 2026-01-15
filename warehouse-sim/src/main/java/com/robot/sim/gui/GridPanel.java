package com.robot.sim.gui;

import com.robot.sim.model.Grid;
import com.robot.sim.model.Node;
import com.robot.sim.model.Robot;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class GridPanel extends JPanel {
    private Grid grid;
    private List<Robot> robots;
    private int cellSize = 15; // Taille d'une case

    private static final Color PATH_COLOR = new Color(0, 150, 255, 60);
    private static final Color TARGET_COLOR = new Color(255, 140, 0);
    private static final Color WALL_COLOR = new Color(60, 63, 65);
    private static final Color FLOOR_COLOR = new Color(245, 245, 245);
    private static final Color GRID_LINE_COLOR = new Color(220, 220, 220);

    public GridPanel(Grid grid, List<Robot> robots) {
        this.grid = grid;
        this.robots = robots;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(FLOOR_COLOR); 
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node node = grid.getNode(x, y);
                if (!node.isWalkable) {
                    g.setColor(WALL_COLOR);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else {
                    g.setColor(GRID_LINE_COLOR);
                    g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }

        for (Robot robot : robots) {
            Node pos = robot.getPosition();
            Node target = robot.getTarget();
            Color rColor = robot.getColor(); 

            List<Node> path = robot.getCurrentPath();
            if (path != null && !path.isEmpty()) {
                g.setColor(PATH_COLOR); // Utilisation de la constante
                try {
                    for (int i = 0; i < path.size(); i++) {
                         Node n = path.get(i);
                         if(n != null) g.fillRect(n.x * cellSize + 5, n.y * cellSize + 5, cellSize - 10, cellSize - 10);
                    }
                } catch (Exception e) {  }
            }

            if (target != null) {
                g.setColor(TARGET_COLOR); 
                int tx = target.x * cellSize;
                int ty = target.y * cellSize;
                g.fillRect(tx + cellSize/2 - 1, ty + 2, 2, cellSize - 4); 
                g.fillRect(tx + 2, ty + cellSize/2 - 1, cellSize - 4, 2); 
            }

            g.setColor(rColor);
            g.fillOval(pos.x * cellSize + 2, pos.y * cellSize + 2, cellSize - 4, cellSize - 4);
            
            g.setColor(Color.BLACK);
            g.drawOval(pos.x * cellSize + 2, pos.y * cellSize + 2, cellSize - 4, cellSize - 4);
        }
    }
    
    public int getPreferredWidth() { return grid.getWidth() * cellSize; }
    public int getPreferredHeight() { return grid.getHeight() * cellSize; }
}