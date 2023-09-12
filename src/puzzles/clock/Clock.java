package puzzles.clock;

import puzzles.common.solver.Solver;

import java.io.IOException;

/**
 * Gets the arguments of the program (hours, start, end)
 * Creates a new ClockConfig (the initial configuration)
 * Creates a new Solver with the initial ClockConfig
 * Displays hours, start, and end hours
 * Calls solver.solve to get the shortest path
 *
 * @author Daniel Baek
 */
public class Clock {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println(("Usage: java Clock start stop"));
        } else {
            int hours = Integer.parseInt(args[0]);
            int start = Integer.parseInt(args[1]);
            int end = Integer.parseInt(args[2]);
            ClockConfig clock = new ClockConfig(hours, start, end);
            Solver solver = new Solver(clock);
            System.out.println("Hours: " + hours + ", Start: " + start + ", End: " + end);
            solver.solve();
            solver.display();
        }
    }
}
