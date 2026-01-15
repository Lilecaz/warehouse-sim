package com.robot.sim.model;

public class Grid {
    private int width;
    private int height;
    private Node[][] nodes;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.nodes = new Node[width][height];
        initGrid();
    }

    private void initGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean isWalkable = Math.random() > 0.1; 
                nodes[x][y] = new Node(x, y, isWalkable);
            }
        }
    }

    public Node getNode(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return nodes[x][y];
        }
        return null;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Node[][] getNodes() { return nodes; }
}