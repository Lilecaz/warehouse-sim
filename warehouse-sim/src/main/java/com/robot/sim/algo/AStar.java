package com.robot.sim.algo;

import com.robot.sim.model.Node;
import java.util.*;

public class AStar {

    /**
     * Classe interne pour isoler les calculs de chaque thread.
     */
    private static class NodeWrapper implements Comparable<NodeWrapper> {
        Node node;
        NodeWrapper parent;
        double gCost = Double.MAX_VALUE;
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

    public static List<Node> findPath(Node start, Node target, Node[][] grid) {
        // Map locale pour ce calcul précis
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
                // Si mur ou déjà traité, on passe
                if (!neighbor.isWalkable || closedSet.contains(neighbor)) {
                    continue;
                }

                // --- LOGIQUE ANTI-BOUCHON ---
                double moveCost = 1.0;
                
                // Si la case est occupée par un autre robot (et ce n'est pas l'arrivée)
                // On met un coût très élevé (50) pour forcer le robot à contourner
                if (neighbor.isOccupied() && !neighbor.equals(target)) {
                    moveCost = 50.0;
                }
                // ----------------------------

                NodeWrapper neighborWrapper = nodeWrappers.computeIfAbsent(neighbor, NodeWrapper::new);
                double newCost = currentWrapper.gCost + moveCost;

                if (newCost < neighborWrapper.gCost) {
                    neighborWrapper.gCost = newCost;
                    neighborWrapper.hCost = getDistance(neighbor, target);
                    neighborWrapper.parent = currentWrapper;

                    // Mise à jour de la PriorityQueue
                    openSet.remove(neighborWrapper);
                    openSet.add(neighborWrapper);
                }
            }
        }
        return null; // Pas de chemin trouvé
    }

    private static List<Node> retracePath(NodeWrapper startWrapper, NodeWrapper endWrapper) {
        List<Node> path = new ArrayList<>();
        NodeWrapper current = endWrapper;

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