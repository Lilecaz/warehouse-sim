package com.robot.sim.model;

import java.util.Objects;

public class Node {
    public int x, y;
    public boolean isWalkable;
    
    public Node parent;
    public double gCost; // Distance depuis le départ
    public double hCost; // Estimation vers l'arrivée (Heuristique)

    private Robot occupant = null;

    public synchronized boolean isOccupied() {
        return occupant != null;
    }

    public synchronized void setOccupant(Robot robot) {
        this.occupant = robot;
    }
    
    public synchronized Robot getOccupant() {
        return occupant;
    }

    public Node(int x, int y, boolean isWalkable) {
        this.x = x;
        this.y = y;
        this.isWalkable = isWalkable;
    }

    public double getFCost() {
        return gCost + hCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}