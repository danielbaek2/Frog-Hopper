package puzzles.strings;

import puzzles.common.solver.Solver;

import java.io.IOException;

/**
 * Gets the arguments of the program (start, end)
 * Creates a new StringsConfig (the initial configuration)
 * Creates a new Solver with the initial StringsConfig
 * Displays start and end strings
 * Calls solver.solve to get the shortest path
 *
 * @author Daniel Baek
 */
public class Strings {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            String start = args[0];
            String end = args[1];
            StringsConfig string = new StringsConfig(start, end);
            Solver solver = new Solver(string);
            System.out.println("Start: " + start + ", End: " + end);
            solver.solve();
            solver.display();
        }
    }
}
