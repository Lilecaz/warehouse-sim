package com.robot.sim.algo;

import com.robot.sim.model.Node;
import com.robot.sim.model.Robot;
import java.util.ArrayList;
import java.util.List;

public class Quadtree {
    private int capacity; // Combien de robots max avant de se diviser ?
    private AABB boundary;
    private List<Robot> robots;
    private boolean divided = false;

    // Enfants
    private Quadtree northWest;
    private Quadtree northEast;
    private Quadtree southWest;
    private Quadtree southEast;

    public Quadtree(AABB boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.robots = new ArrayList<>();
    }

    public boolean insert(Robot robot) {
        Node pos = robot.getPosition();

        // Si le robot n'est pas dans notre zone, on l'ignore
        if (!boundary.contains(pos)) {
            return false;
        }

        // Si on a de la place et qu'on n'est pas divisé, on l'ajoute ici
        if (robots.size() < capacity && !divided) {
            robots.add(robot);
            return true;
        }

        // Sinon, on se divise (si ce n'est pas déjà fait)
        if (!divided) {
            subdivide();
        }

        // Et on essaie de l'insérer dans l'un des enfants
        if (northWest.insert(robot)) return true;
        if (northEast.insert(robot)) return true;
        if (southWest.insert(robot)) return true;
        if (southEast.insert(robot)) return true;

        return false; 
    }

    private void subdivide() {
        double x = boundary.x;
        double y = boundary.y;
        double h = boundary.halfDimension / 2;

        northWest = new Quadtree(new AABB(x - h, y - h, h), capacity);
        northEast = new Quadtree(new AABB(x + h, y - h, h), capacity);
        southWest = new Quadtree(new AABB(x - h, y + h, h), capacity);
        southEast = new Quadtree(new AABB(x + h, y + h, h), capacity);
        
        for (Robot r : robots) {
             boolean inserted = northWest.insert(r) || northEast.insert(r) || 
                                southWest.insert(r) || southEast.insert(r);
        }
        robots.clear(); 
        
        divided = true;
    }

    public List<Robot> query(AABB range, List<Robot> found) {
        if (found == null) found = new ArrayList<>();

        if (!boundary.intersects(range)) {
            return found;
        }

        for (Robot r : robots) {
            if (range.contains(r.getPosition())) {
                found.add(r);
            }
        }

        if (divided) {
            northWest.query(range, found);
            northEast.query(range, found);
            southWest.query(range, found);
            southEast.query(range, found);
        }

        return found;
    }
}