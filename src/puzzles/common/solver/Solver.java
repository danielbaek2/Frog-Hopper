package puzzles.common.solver;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Solves the Configuration puzzles
 *
 * @author Daniel Baek
 */
public class Solver {
    private final Configuration start;
    private Configuration end;
    private int count = 1;
    private List<Configuration> path;
    private HashMap<Configuration, Configuration> predecessors;
    /**
     * Creates a new solver, only storing the start Configuration
     *
     * @param start = start configuration
     */
    public Solver(Configuration start) {
        this.start = start;
        this.end = null;
    }
    public List<Configuration> getPath() {
        return this.path;
    }
    /**
     * Performs breadth first search and creates the shortest path to the end configuration
     */
    public void solve() {
        List<Configuration> queue = new LinkedList<>();
        queue.add(start);
        predecessors = new HashMap<>();
        predecessors.put(start, null);
        while (!queue.isEmpty()) {
            Configuration current = queue.remove(0);
            if (current.isSolution()) {
                end = current;
                break;
            }
            for (Configuration config : current.getNeighbors()) {
                count += 1;
                if (!predecessors.containsKey(config)) {
                    predecessors.put(config, current);
                    queue.add(config);
                }
            }
        }
        path = new LinkedList<>();
        if (predecessors.containsKey(end)) {
            Configuration current = end;
            while (current != start) {
                path.add(0, current);
                current = predecessors.get(current);
            }
            path.add(0, start);
        }
    }
    public void display() {
        System.out.println("Total configs: " + count);
        System.out.println("Unique configs: " + predecessors.size());
        // If there is no path to the end configuration, "no solution" will be displayed
        if (path.size() == 0) {
            System.out.println("No solution");
        }
        // If there is a path, each step will be displayed
        for (int i = 0; i < path.size(); i++) {
            System.out.println("Step " + i + ": " + path.get(i));
        }
    }
}