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

        g.setColor(new Color(240, 240, 240)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Node node = grid.getNode(x, y);
                if (!node.isWalkable) {
                    g.setColor(new Color(60, 63, 65)); 
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else {
                    g.setColor(new Color(200, 200, 200));
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
                g.setColor(new Color(rColor.getRed(), rColor.getGreen(), rColor.getBlue(), 60));
                for (Node n : path) {
                    g.fillRect(n.x * cellSize + 5, n.y * cellSize + 5, cellSize - 10, cellSize - 10);
                }
            }

            if (target != null) {
                g.setColor(rColor);
                int tx = target.x * cellSize;
                int ty = target.y * cellSize;
                g.drawLine(tx + 2, ty + 2, tx + cellSize - 2, ty + cellSize - 2);
                g.drawLine(tx + 2, ty + cellSize - 2, tx + cellSize - 2, ty + 2);
            }

            g.setColor(rColor);
            g.fillOval(pos.x * cellSize + 2, pos.y * cellSize + 2, cellSize - 4, cellSize - 4);
            
            g.setColor(Color.BLACK);
            g.drawOval(pos.x * cellSize + 2, pos.y * cellSize + 2, cellSize - 4, cellSize - 4);
            
            g.drawString("" + robot.getId(), pos.x * cellSize, pos.y * cellSize);
        }
    }
    
    public int getPreferredWidth() { return grid.getWidth() * cellSize; }
    public int getPreferredHeight() { return grid.getHeight() * cellSize; }
}