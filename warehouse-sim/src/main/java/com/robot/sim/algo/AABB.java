package com.robot.sim.algo;

import com.robot.sim.model.Node;

public class AABB {
    public double x, y; 
    public double halfDimension; 

    public AABB(double x, double y, double halfDimension) {
        this.x = x;
        this.y = y;
        this.halfDimension = halfDimension;
    }

    public boolean contains(Node node) {
        return (node.x >= x - halfDimension &&
                node.x <= x + halfDimension &&
                node.y >= y - halfDimension &&
                node.y <= y + halfDimension);
    }

    public boolean intersects(AABB other) {
        return !(other.x - other.halfDimension > x + halfDimension ||
                 other.x + other.halfDimension < x - halfDimension ||
                 other.y - other.halfDimension > y + halfDimension ||
                 other.y + other.halfDimension < y - halfDimension);
    }
}