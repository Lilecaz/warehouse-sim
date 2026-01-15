package com.robot.sim.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Robot {
    private int id;
    private Node position;
    private Node target;
    private List<Node> currentPath;
    private Color color;

    public Robot(int id, Node startPosition) {
        this.id = id;
        this.position = startPosition;
        this.currentPath = new ArrayList<>();
        this.color = new Color((int)(Math.random() * 0x1000000));
    }

    public int getId() { return id; }
    
    public Node getPosition() { return position; }
    public void setPosition(Node position) { this.position = position; }

    public Node getTarget() { return target; }
    public void setTarget(Node target) { this.target = target; }

    public List<Node> getCurrentPath() { return currentPath; }
    public void setCurrentPath(List<Node> currentPath) { this.currentPath = currentPath; }
    public void setColor(Color color) {
        this.color = color;
    }
    public Color getColor() { return color; }
}