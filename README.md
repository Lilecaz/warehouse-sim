# Multi-Agent Warehouse Simulator

A high-performance Java simulation engine capable of managing hundreds of autonomous robots in a logistics grid. This project explores multi-threading, pathfinding algorithms, and spatial optimization to solve complex traffic congestion problems in real-time.

## Project Overview

This application simulates a warehouse environment where agents (robots) must navigate from a starting point to a dynamically assigned target. The core challenge addressed is the "Gridlock" phenomenon common in distributed robotic systems. The engine solves this using a decentralized decision-making process involving custom A* pathfinding, deadlock detection, and spatial partitioning.

## Key Features

* **Custom A* Pathfinding**: Implementation of the A* algorithm from scratch, optimized for grid-based navigation with dynamic weighting.
* **Multi-Threaded Architecture**: Each robot operates as an independent agent (Thread) with its own decision-making loop, simulating a true distributed system.
* **Spatial Optimization (Quadtree)**: Implementation of a Quadtree structure to handle proximity queries efficiently (O(N log N)), allowing for real-time density analysis.
* **Advanced Collision Avoidance**: A hybrid strategy to resolve deadlocks and traffic jams without central control.
* **Real-time Visualization**: Swing-based GUI with dynamic rendering of paths, targets, and agent states.

## Algorithmic Logic & Deadlock Resolution

To prevent traffic jams when hundreds of agents share a confined space, the system implements a **3-Level Patience Strategy**:

1.  **Dynamic Path Costing (Prevention)**
    The A* algorithm treats occupied cells not as walls, but as "high-cost" terrain. If a cell is occupied by another robot, its traversal cost increases significantly (Cost = 50 vs. Cost = 1). This causes robots to naturally curve around traffic rather than joining a queue.

2.  **Patience Escalation (Reaction)**
    Each agent maintains an internal "patience" counter when blocked:
    * **Low Patience:** The robot waits for the path to clear.
    * **Medium Patience:** The robot triggers a full path recalculation to find a detour using the current grid state.
    * **High Patience (Heuristic Move):** The robot attempts a random move to a free adjacent cell to break the deadlock structure physically.

3.  **Failsafe Teleportation**
    In extreme scenarios where an agent is completely surrounded for an extended period, a respawn mechanism is triggered to maintain simulation fluidity and prevent memory issues.

## Technical Architecture

The project follows a strict separation of concerns:

* **com.robot.sim.model**: Thread-safe data structures representing the state of the simulation (Grid, Node, Robot).
* **com.robot.sim.algo**: Pure algorithmic logic containing the A* implementation and Quadtree spatial partitioning.
* **com.robot.sim.engine**: Business logic where the `RobotAgent` (Runnable) handles the state machine (Move, Wait, Recalculate).
* **com.robot.sim.gui**: Visualization layer using Java Swing for the main loop and rendering.

## Getting Started

### Prerequisites

* Java JDK 21 or higher
* Maven

### Installation

1.  Clone the repository:
    ```bash
    git clone [https://github.com/Lilecaz/warehouse-sim.git](https://github.com/Lilecaz/warehouse-sim.git)
    cd warehouse-sim
    ```

2.  Build the project using Maven:
    ```bash
    mvn clean install
    ```

3.  Run the simulation:
    ```bash
    java -cp target/classes com.robot.sim.App
    ```

### Usage

1.  Upon launching the application, a dialog box will appear.
2.  Input the desired number of robots (e.g., 100).
3.  The simulation will generate the grid and agents.
    * **Green Agents**: Free flowing traffic.
    * **Red Agents**: High density area detected by the Quadtree.
    * **Blue Trails**: Project path of the agent.
    * **Orange Crosses**: Agent destination.

## Future Improvements

* Implementation of "Pickup" and "Delivery" zones to simulate specific warehouse tasks.
* Implementation of a prioritization system where certain robots have the right of way.
* Migration of the rendering engine to JavaFX or LibGDX for better graphical performance with 1000+ agents.

## License

This project is open-source and available under the MIT License.