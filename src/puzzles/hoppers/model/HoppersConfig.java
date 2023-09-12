package puzzles.hoppers.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import puzzles.common.solver.Configuration;

/**
 * The Configuration of the Hoppers puzzle
 *
 * @author Daniel Baek
 */
public class HoppersConfig implements Configuration{
    char LILYPAD = '.';
    char WATER = '*';
    char GREENFROG = 'G';
    char REDFROG = 'R';
    private int row;
    private int col;
    private final int rowDIM;
    private final int colDIM;
    private int numGreen;
    private final char[][] board;
    /**
     * Creates HoppersConfig
     *
     * @param filename = file
     */
    public HoppersConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String[] DIM = in.readLine().split(" ");
            rowDIM = Integer.parseInt(DIM[0]);
            colDIM = Integer.parseInt(DIM[1]);
            this.board = new char[rowDIM][colDIM];
            for (int row = 0; row < rowDIM; row++) {
                Arrays.fill(this.board[row], WATER);
            }
            this.row = 0;
            this.col = -1;
            for (int r = 0; r < rowDIM; r++) {
                String[] field = in.readLine().split(" ");
                for (int c = 0; c < colDIM; c++) {
                    switch (field[c]) {
                        case "." -> this.board[r][c] = LILYPAD;
                        case "G" -> {
                            this.board[r][c] = GREENFROG;
                            numGreen += 1;
                        }
                        case "R" -> this.board[r][c] = REDFROG;
                    }
                }
            }

        }
    }
    /**
     * Copy constructor, used to make neighbors
     *
     * @param other = parent config
     */
    private HoppersConfig(HoppersConfig other) {
        this.rowDIM = other.rowDIM;
        this.colDIM = other.colDIM;
        this.row = other.row;
        this.col = other.col;
        this.numGreen = other.numGreen;

        this.col += 1;
        if (this.col == colDIM) {
            this.row += 1;
            this.col = 0;
        }
        this.board = new char[rowDIM][colDIM];
        for (int r = 0; r < rowDIM; r++) {
            System.arraycopy(other.board[r], 0, this.board[r], 0, colDIM);
        }
    }

    /**
     * Checks if config is the end config, number of green frogs must be 0
     *
     * @return numGreen == 0
     */
    @Override
    public boolean isSolution() {
        return numGreen == 0;
    }
    /**
     * Gets the diagonal neighbors of the config, used for odd and even rows and cols
     *
     * @param row = row of cell
     * @param col = col of cell
     * @return neighbors
     */
    public Collection<HoppersConfig> diagNeighbors(int row, int col) {
        ArrayList<HoppersConfig> neighbors = new ArrayList<>();
        if (this.board[row][col] == LILYPAD) {
            return neighbors;
        }
        // checks diagonal top left cell
        if ((row - 2 >= 0 && col - 2 >= 0) && (this.board[row-1][col-1] == GREENFROG) &&
               (this.board[row-2][col-2] == LILYPAD)) {
           HoppersConfig neighbor = new HoppersConfig(this);
           if (neighbor.board[row][col] == GREENFROG) {
               neighbor.board[row - 2][col - 2] = GREENFROG;
           } else if (neighbor.board[row][col] == REDFROG) {
               neighbor.board[row - 2][col - 2] = REDFROG;
           }
           neighbor.board[row][col] = LILYPAD;
           neighbor.board[row-1][col-1] = LILYPAD;
           neighbor.numGreen -= 1;
           neighbors.add(neighbor);
        // checks diagonal bottom right cell
        } if ((row + 2 < rowDIM && col + 2 < colDIM) && (this.board[row+1][col+1] == GREENFROG) &&
                (this.board[row+2][col+2] == LILYPAD)) {
           HoppersConfig neighbor = new HoppersConfig(this);
           if (neighbor.board[row][col] == GREENFROG) {
               neighbor.board[row + 2][col + 2] = GREENFROG;
           } else if (neighbor.board[row][col] == REDFROG) {
               neighbor.board[row + 2][col + 2] = REDFROG;
           }
           neighbor.board[row][col] = LILYPAD;
           neighbor.board[row + 1][col + 1] = LILYPAD;
           neighbor.numGreen -= 1;
           neighbors.add(neighbor);
        // checks diagonal top right cell
        } if ((row - 2 >= 0 && col + 2 < colDIM) && (this.board[row-1][col+1] == GREENFROG) &&
               (this.board[row-2][col+2] == LILYPAD)) {
           HoppersConfig neighbor = new HoppersConfig(this);
           if (neighbor.board[row][col] == GREENFROG) {
               neighbor.board[row - 2][col + 2] = GREENFROG;
           } else if (neighbor.board[row][col] == REDFROG) {
               neighbor.board[row - 2][col + 2] = REDFROG;
           }
           neighbor.board[row][col] = LILYPAD;
           neighbor.board[row - 1][col + 1] = LILYPAD;
           neighbor.numGreen -= 1;
           neighbors.add(neighbor);
        // checks diagonal bottom left cell
        } if ((row + 2 < rowDIM && col - 2 >= 0) && (this.board[row+1][col-1] == GREENFROG) &&
                (this.board[row+2][col-2] == LILYPAD)) {
           HoppersConfig neighbor = new HoppersConfig(this);
           if (neighbor.board[row][col] == GREENFROG) {
               neighbor.board[row + 2][col - 2] = GREENFROG;
           } else if (neighbor.board[row][col] == REDFROG) {
               neighbor.board[row + 2][col - 2] = REDFROG;
           }
           neighbor.board[row][col] = LILYPAD;
           neighbor.board[row + 1][col - 1] = LILYPAD;
           neighbor.numGreen -= 1;
           neighbors.add(neighbor);
        }
        return neighbors;
    }
    /**
     * Gets horizontal and vertical neighbors of config, only used for even rows and cols
     *
     * @param row = row of cell
     * @param col = col of cell
     * @return neighbors
     */
    public Collection<HoppersConfig> horizontalVerticalNeighbors(int row, int col) {
        ArrayList<HoppersConfig> neighbors = new ArrayList<>();
        if (this.board[row][col] == LILYPAD) {
            return neighbors;
        }
        // checks far south cell
        if ((row + 4 < rowDIM) && (this.board[row + 4][col] == LILYPAD) && (this.board[row + 2][col] == GREENFROG)) {
            HoppersConfig neighbor = new HoppersConfig(this);
            if (neighbor.board[row][col] == GREENFROG) {
                neighbor.board[row + 4][col] = GREENFROG;
            } else if (neighbor.board[row][col] == REDFROG) {
                neighbor.board[row + 4][col] = REDFROG;
            }
            neighbor.board[row][col] = LILYPAD;
            neighbor.board[row + 2][col] = LILYPAD;
            neighbor.numGreen -= 1;
            neighbors.add(neighbor);
        // checks far north cell
        } if ((row - 4 >= 0) && (this.board[row - 4][col] == LILYPAD) && (this.board[row - 2][col] == GREENFROG)) {
            HoppersConfig neighbor = new HoppersConfig(this);
            if (neighbor.board[row][col] == GREENFROG) {
                neighbor.board[row - 4][col] = GREENFROG;
            } else if (neighbor.board[row][col] == REDFROG) {
                neighbor.board[row - 4][col] = REDFROG;
            }
            neighbor.board[row][col] = LILYPAD;
            neighbor.board[row - 2][col] = LILYPAD;
            neighbor.numGreen -= 1;
            neighbors.add(neighbor);
        // check far left cell
        } if ((col - 4 >= 0) && (this.board[row][col - 4] == LILYPAD) && (this.board[row][col - 2] == GREENFROG)) {
            HoppersConfig neighbor = new HoppersConfig(this);
            if (neighbor.board[row][col] == GREENFROG) {
                neighbor.board[row][col - 4] = GREENFROG;
            } else if (neighbor.board[row][col] == REDFROG) {
                neighbor.board[row][col - 4] = REDFROG;
            }
            neighbor.board[row][col] = LILYPAD;
            neighbor.board[row][col - 2] = LILYPAD;
            neighbor.numGreen -= 1;
            neighbors.add(neighbor);
        // check far right cell
        } if ((col + 4 < colDIM) && (this.board[row][col + 4] == LILYPAD) && (this.board[row][col + 2] == GREENFROG)) {
            HoppersConfig neighbor = new HoppersConfig(this);
            if (neighbor.board[row][col] == GREENFROG) {
                neighbor.board[row][col + 4] = GREENFROG;
            } else if (neighbor.board[row][col] == REDFROG) {
                neighbor.board[row][col + 4] = REDFROG;
            }
            neighbor.board[row][col] = LILYPAD;
            neighbor.board[row][col + 2] = LILYPAD;
            neighbor.numGreen -= 1;
            neighbors.add(neighbor);
        }
        return neighbors;
    }
    /**
     * Gets the neighbors of the cell
     *
     * @return successors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> successors = new LinkedList<>();
        for (int row = 0; row < rowDIM; row++) {
            for (int col = 0; col < colDIM; col++) {
                successors.addAll(this.diagNeighbors(row, col));
                if (row % 2 == 0 && col % 2 == 0) {
                    successors.addAll(this.horizontalVerticalNeighbors(row, col));
                }
            }
        }
        return successors;
    }
    /**
     * Gets the hash code of the config
     *
     * @return hash code of config
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.board);
    }
    /**
     * Checks if config is equal to other config
     *
     * @param other = other config
     * @return True if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof HoppersConfig h) {
            result = Arrays.deepEquals(this.board, h.board);
        }
        return result;
    }
    /**
     * Gets String representation of config
     *
     * @return result
     */
    public String getDisplay() {
        StringBuilder result = new StringBuilder(" ");
        result.append(System.lineSeparator());
        for (int row = 0; row < rowDIM ; row++) {
            for (int col = 0; col < colDIM ; col++) {
                if (col != colDIM - 1) {
                    result.append(getCell(row, col)).append(" ");
                } else {
                    result.append(getCell(row, col)).append(System.lineSeparator());
                }
            }
        }
        return result.toString();
    }
    /**
     * Gets the cell at the given coordinate
     *
     * @param row = row
     * @param col = col
     * @return cell
     */
    public char getCell(int row, int col) {
        return this.board[row][col];
    }
    /**
     * Gets the rowDim
     *
     * @return this.rowDIM
     */
    public int getRowDIM() {
        return this.rowDIM;
    }
    /**
     * Gets the colDim
     *
     * @return this.colDIM
     */
    public int getColDIM() {
        return this.colDIM;
    }
    /**
     * Gets String representation
     *
     * @return getDisplay()
     */
    @Override
    public String toString() {
        return getDisplay();
    }
}
