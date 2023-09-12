package puzzles.clock;

import puzzles.common.solver.Configuration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * The Configuration of a clock
 *
 * @author Daniel Baek
 */
public class ClockConfig implements Configuration {
    private static int hours;
    private static int end;
    private final int child;
    /**
     * Creates a new ClockConfig, mainly used to generate initial (main) ClockConfig
     *
     * @param hours = number of hours the clock has
     * @param child = the starting hour
     * @param end = the finish hour
     */
    public ClockConfig(int hours, int child, int end) {
        ClockConfig.hours = hours;
        this.child = child;
        ClockConfig.end = end;
    }
    /**
     * Creates a new ClockConfig, mainly used to generate neighbors
     *
     * @param current = the current hour
     */
    public ClockConfig(int current) {
        this.child = current;
    }
    /**
     * Checks if the current hour is equal to the finish hour
     *
     * @return True if it is a solution, false otherwise
     */
    @Override
    public boolean isSolution() {
        return end == this.child;
    }
    /**
     * Gets the neighbors of the current hour
     *
     * @return neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        int neighbor1 = this.child - 1;
        if (neighbor1 <= 0) {
            neighbor1 = hours;
        }
        int neighbor2 = this.child + 1;
        if (neighbor2 == hours + 1) {
            neighbor2 = 1;
        }
        ClockConfig neighborOne = new ClockConfig(neighbor1);
        ClockConfig neighborTwo = new ClockConfig(neighbor2);
        neighbors.add(neighborOne);
        neighbors.add(neighborTwo);
        return neighbors;
    }
    /**
     * Checks if the current hour is equal to the other hour
     *
     * @param other = other hour
     * @return True if they are the same, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof ClockConfig c) {
            result = this.child == c.child;
        }
        return result;
    }
    /**
     * Gets the hash code for the child, or current, hour
     *
     * @return hash code of child
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.child);
    }
    /**
     * Represents the ClockConfig as the child value
     *
     * @return String representation of child
     */
    @Override
    public String toString() {
        return String.valueOf(this.child);
    }
}
