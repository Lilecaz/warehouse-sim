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
    private int cellSize = 15;

    public GridPanel(Grid grid, List<Robot> robots) {
        this.grid = grid;
        this.robots = robots;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node node = grid.getNode(x, y);
                if (!node.isWalkable) {
                    g.setColor(Color.BLACK); // Mur
                } else {
                    g.setColor(Color.WHITE); // Libre
                }
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                
                g.setColor(new Color(230, 230, 230));
                g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }

        for (Robot robot : robots) {
            g.setColor(new Color(robot.getColor().getRed(), robot.getColor().getGreen(), robot.getColor().getBlue(), 100));
            List<Node> path = robot.getCurrentPath();
            if (path != null) {
                try {
                    for (int i = 0; i < path.size(); i++) {
                        Node n = path.get(i);
                        if (n != null) {
                            g.fillRect(n.x * cellSize + 4, n.y * cellSize + 4, cellSize - 8, cellSize - 8);
                        }
                    }
                } catch (Exception e) {
                }
            }


            Node pos = robot.getPosition();
            
            g.setColor(robot.getColor());
            g.fillOval(pos.x * cellSize + 2, pos.y * cellSize + 2, cellSize - 4, cellSize - 4);
            
            g.setColor(Color.BLACK);
            g.drawOval(pos.x * cellSize + 2, pos.y * cellSize + 2, cellSize - 4, cellSize - 4);
        }
    }
    
    public int getPreferredWidth() { return grid.getWidth() * cellSize; }
    public int getPreferredHeight() { return grid.getHeight() * cellSize; }
}