package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.io.IOException;
import java.util.*;

/**
 * The Model of the Hoppers puzzle
 *
 * @author Daniel Baek
 */
public class HoppersModel { 
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private HoppersConfig currentConfig;
    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }
    /**
     * Gets the current configuration
     *
     * @return this.currentConfig
     */
    public HoppersConfig getCurrentConfig() {
        return this.currentConfig;
    }
    /**
     * Gives the user a hint, the next step, for the puzzle
     */
    public void hint() {
        Solver solver = new Solver(this.currentConfig);
        solver.solve();
        List<Configuration> path = solver.getPath();
        if (path.isEmpty()) {
            alertObservers("END");
            return;
        }
        path.remove(0);
        if (!path.isEmpty()) {
            this.currentConfig = (HoppersConfig) path.remove(0);
        }
        alertObservers("HINT");
    }
    /**
     * Resets the puzzle to the initial state
     *
     * @param file = file
     */
    public void reset(String file) {
        try {
            this.currentConfig = new HoppersConfig(file);
        } catch (IOException ignored) {
        }
        alertObservers("RESET");
    }
    /**
     * Loads the file
     *
     * @param file = file
     */
    public void load(String file) {
        try {
            if (!file.contains("/data")) {
                alertObservers("NO FILE");
            }
            this.currentConfig = new HoppersConfig(file);
            alertObservers("NEW PUZZLE");
        } catch (IOException ignored) {
        }
    }
    /**
     * Selects the initial cell and moves it the final cell
     *
     * @param initialRow = initial row
     * @param initialCol = initial col
     * @param finalRow = final row
     * @param finalCol = final col
     */
    public void select(int initialRow, int initialCol, int finalRow, int finalCol) {
        if (initialRow < 0 && initialCol < 0) {
            alertObservers("NEW CONFIG");
            return;
        }
        ArrayList<HoppersConfig> neighbors = new ArrayList<>(this.currentConfig.diagNeighbors(initialRow, initialCol));
        if (initialRow % 2 == 0 && initialCol % 2 == 0) {
            neighbors.addAll(this.currentConfig.horizontalVerticalNeighbors(initialRow, initialCol));
        }
        if (neighbors.isEmpty()) {
            alertObservers("SAME CONFIG");
            return;
        }
        for (HoppersConfig config : neighbors) {
            if ((config.getCell(finalRow, finalCol) == config.GREENFROG || config.getCell(finalRow, finalCol) == config.REDFROG) && !currentConfig.equals(config)) {
                this.currentConfig = config;
                alertObservers("NEW CONFIG");
                break;
            }
            alertObservers("SAME CONFIG");
        }
    }

    /**
     * Creates the HoppersModel
     *
     * @param filename = file
     */
    public HoppersModel(String filename) throws IOException {
        this.currentConfig = new HoppersConfig(filename);
    }
}
