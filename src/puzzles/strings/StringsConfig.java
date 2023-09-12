package puzzles.strings;

import puzzles.common.solver.Configuration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * The Configuration of a string
 *
 * @author Daniel Baek
 */
public class StringsConfig implements Configuration {
    private final String child;
    private static String end;
    /**
     * Creates a new StringsConfig, mainly used to generate the initial (main) StringsConfig
     *
     * @param child = child String
     * @param end = finish String
     */
    public StringsConfig(String child, String end) {
        this.child = child;
        StringsConfig.end = end;
    }
    /**
     * Creates a new StringsConfig, mainly used to generate the neighbor StringsConfig
     *
     * @param child = child String
     */
    public StringsConfig(String child) {
        this.child = child;
    }
    /**
     * Checks if the child (current) String is equal to the finish String
     *
     * @return True if they are the same, false otherwise
     */
    @Override
    public boolean isSolution() {
        return this.child.equals(end);
    }
    /**
     * Gets the neighbor StringConfigs of the child String
     *
     * @return neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        for (int c = 0; c < this.child.length(); c++) {
            char main = this.child.charAt(c);
            char c1 = (char) (main + 1);
            if (c1 == '[') {
                c1 = 'A';
            }
            StringBuilder string1 = new StringBuilder(this.child);
            string1.setCharAt(c, c1);
            char c2 = (char) (main - 1);
            if (c2 == '@') {
                c2 = 'Z';
            }
            StringBuilder string2 = new StringBuilder(this.child);
            string2.setCharAt(c, c2);
            StringsConfig cOne = new StringsConfig(String.valueOf(string1));
            StringsConfig cTwo = new StringsConfig(String.valueOf(string2));
            neighbors.add(cTwo);
            neighbors.add(cOne);
        }
        return neighbors;
    }
    /**
     * Checks if the child String is equal to the other
     *
     * @param other = other String
     * @return True if they are the same, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof StringsConfig s) {
            result = this.child.equals(s.child);
        }
        return result;
    }
    /**
     * Gets the hash code for the child String
     *
     * @return hash code for child String
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.child);
    }
    /**
     * Represents the StringsConfig as the child value
     *
     * @return child String
     */
    @Override
    public String toString() {
        return this.child;
    }
}
