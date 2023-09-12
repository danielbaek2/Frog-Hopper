package puzzles.hoppers.solver;

import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

import java.io.IOException;

public class Hoppers {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        } else {
            System.out.println("File: " + args[0]);
            try {
                HoppersConfig hopper = new HoppersConfig(args[0]);
                System.out.println(hopper);
                Solver solver = new Solver(hopper);
                solver.solve();
                solver.display();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
