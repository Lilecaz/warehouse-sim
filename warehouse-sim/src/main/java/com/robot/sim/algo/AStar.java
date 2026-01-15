package com.robot.sim.algo;

import com.robot.sim.model.Node;
import java.util.*;

public class AStar {

    /**
     * Classe interne pour stocker les données de calcul A* (Coûts, Parent)
     * de manière isolée pour chaque Thread.
     * Ainsi, la grille (Node) reste propre et partagée.
     */
    private static class NodeWrapper implements Comparable<NodeWrapper> {
        Node node;
        NodeWrapper parent;
        double gCost = Double.MAX_VALUE; // Infini par défaut
        double hCost = 0;

        public NodeWrapper(Node node) {
            this.node = node;
        }

        public double getFCost() {
            return gCost + hCost;
        }

        @Override
        public int compareTo(NodeWrapper other) {
            return Double.compare(this.getFCost(), other.getFCost());
        }
    }

    public static List<Node> findPath(Node start, Node target, Node[][] grid, boolean avoidOthers) {
        Map<Node, NodeWrapper> nodeWrappers = new HashMap<>();
        PriorityQueue<NodeWrapper> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        NodeWrapper startWrapper = new NodeWrapper(start);
        startWrapper.gCost = 0;
        nodeWrappers.put(start, startWrapper);
        openSet.add(startWrapper);

        while (!openSet.isEmpty()) {
            NodeWrapper currentWrapper = openSet.poll();
            Node current = currentWrapper.node;

            if (current.equals(target)) {
                return retracePath(startWrapper, currentWrapper);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current, grid)) {
                boolean isOccupiedByOther = avoidOthers && neighbor.isOccupied() && !neighbor.equals(target);
                
                if (!neighbor.isWalkable || closedSet.contains(neighbor) || isOccupiedByOther) {
                    continue;
                }

                NodeWrapper neighborWrapper = nodeWrappers.computeIfAbsent(neighbor, NodeWrapper::new);
                double newCost = currentWrapper.gCost + getDistance(current, neighbor);

                if (newCost < neighborWrapper.gCost) {
                    neighborWrapper.gCost = newCost;
                    neighborWrapper.hCost = getDistance(neighbor, target);
                    neighborWrapper.parent = currentWrapper;
                    openSet.remove(neighborWrapper);
                    openSet.add(neighborWrapper);
                }
            }
        }
        return null;
    }
    private static List<Node> retracePath(NodeWrapper startWrapper, NodeWrapper endWrapper) {
        List<Node> path = new ArrayList<>();
        NodeWrapper current = endWrapper;

        // On remonte la chaîne des parents via les Wrappers
        while (current != startWrapper) {
            path.add(current.node);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static double getDistance(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static List<Node> getNeighbors(Node node, Node[][] grid) {
        List<Node> neighbors = new ArrayList<>();
        int width = grid.length;
        int height = grid[0].length;
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        for (int i = 0; i < 4; i++) {
            int nx = node.x + dx[i];
            int ny = node.y + dy[i];

            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                neighbors.add(grid[nx][ny]);
            }
        }
        return neighbors;
    }
}