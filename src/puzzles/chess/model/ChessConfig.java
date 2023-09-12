package puzzles.chess.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The full representation of a configuration in the Chess puzzle
 * It can read an initial config from a file and supports the Config
 * methods necessary for the solver
 *
 * @author Erica Wu <ew3797>
 */
public class ChessConfig implements Configuration {
    /** char representing an empty cell */
    public final static char EMPTY = '.';
    /** char representing a king piece */
    public final static char KING = 'K';
    /** char representing a queen piece */
    public final static char QUEEN = 'Q';
    /** char representing a knight piece */
    public final static char KNIGHT = 'N';
    /** char representing a bishop piece */
    public final static char BISHOP = 'B';
    /** char representing a rook piece */
    public final static char ROOK = 'R';
    /** char representing a pawn piece */
    public final static char PAWN = 'P';

    /** 2D char array representing the chess board */
    private final char[][] board;
    /** column dimension of the board */
    public static int colDim;
    /** row dimension of the board */
    public static int rowDim;

    /**
     * Construct the initial config from an input file
     * @param filename name of file to read from
     * @throws IOException if the file is not found or there are errors reading
     */
    public ChessConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String[] fields = in.readLine().split("\\s+");
            rowDim = Integer.parseInt(fields[0]);
            colDim = Integer.parseInt(fields[1]);
            this.board = new char[rowDim][colDim];

            for (int i = 0; i < rowDim; i++) {
                fields = in.readLine().split("\\s+");
                for (int j = 0; j < colDim; j++) {
                    board[i][j] = fields[j].charAt(0);
                }
            }
        }
    }

    /**
     * Copy constructor
     * Takes a config and copies the board
     * @param other
     */
    public ChessConfig(ChessConfig other) {
        char[][] copy = new char[rowDim][colDim];
        for (int i = 0; i < rowDim; i++) {
            copy[i] = Arrays.copyOf(other.board[i], other.board[i].length);
        }
        this.board = copy;
    }

    /**
     * Count the number of pieces on the board (not EMPTY)
     * @return number of pieces still on the board
     */
    public int pieceCounter() {
        int pieces = 0;
        for (int i = 0; i < rowDim; i++) {
            for (int j = 0; j < colDim; j++) {
                if (this.board[i][j] != EMPTY) {
                    pieces += 1;
                }
            }
        }
        return pieces;
    }

    /**
     * Is the current config the solution or not?
     * @return true if it is; false otherwise
     */
    @Override
    public boolean isSolution() {
        return this.pieceCounter() == 1;
    }

    /**
     * Get the collection of successors from the current one
     * @return all possible successors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> allNeighbors = new ArrayList<>();
        for (int i = 0; i < rowDim; i++) {
            for (int j = 0; j < colDim; j++) {
                char currentPos = this.board[i][j];
                if (currentPos == KING) {
                    allNeighbors.addAll(kNeighbor(i, j));
                } else if (currentPos == QUEEN) {
                    allNeighbors.addAll(qNeighbor(i, j));
                } else if (currentPos == KNIGHT) {
                    allNeighbors.addAll(knNeighbor(i, j));
                } else if (currentPos == BISHOP) {
                    allNeighbors.addAll(bNeighbor(i, j));
                } else if (currentPos == ROOK) {
                    allNeighbors.addAll(rNeighbor(i, j));
                } else if (currentPos == PAWN) {
                    allNeighbors.addAll(pNeighbor(i, j));
                }
            }
        }
        Set<Configuration> noNullNeighbors = new LinkedHashSet<>();
        for (Configuration config : allNeighbors) {
            if (config != null) {
                noNullNeighbors.add(config);
            }
        }
        return Collections.unmodifiableSet(noNullNeighbors);
    }

    /**
     * Get the piece at the cell (row, col) on the board
     * @param row row of cell
     * @param col col of cell
     * @return piece at (row, col) on the board
     */
    public char getCell(int row, int col){
        return this.board[row][col];
    }

    /**
     * helper function to find all possible successors for a king
     * @param row row of the cell containing the king piece
     * @param col col of the cell containing the king piece
     * @return all possible captures that can be made by the king
     */
    private Collection<Configuration> kNeighbor(int row, int col) {
        Collection<Configuration> neighbors = new ArrayList<>();
        //north
        neighbors.add(mover(row, col, row - 1, col));
        //south
        neighbors.add(mover(row, col, row + 1, col));
        //west
        neighbors.add(mover(row, col, row, col - 1));
        //east
        neighbors.add(mover(row, col, row, col + 1));
        //NE
        neighbors.add(mover(row, col, row - 1, col + 1));
        //NW
        neighbors.add(mover(row, col, row - 1, col - 1));
        //SE
        neighbors.add(mover(row, col, row + 1, col + 1));
        //SW
        neighbors.add(mover(row, col, row + 1, col - 1));
        return neighbors;
    }

    /**
     * helper function to find all possible successors for a queen
     * @param row row of the cell containing the queen piece
     * @param col col of the cell containing the queen piece
     * @return all possible captures that can be made by the queen
     */
    private Collection<Configuration> qNeighbor(int row, int col) {
        Collection<Configuration> neighbors = new ArrayList<>();
        neighbors.addAll(bNeighbor(row, col));
        neighbors.addAll(rNeighbor(row, col));
        return neighbors;
    }

    /**
     * helper function to find all possible successors for a knight
     * @param row row of the cell containing the knight piece
     * @param col col of the cell containing the knight piece
     * @return all possible captures that can be made by the knight
     */
    private Collection<Configuration> knNeighbor(int row, int col) {
        Collection<Configuration> neighbors = new ArrayList<>();
        //NE
        neighbors.add(mover(row, col, row - 2, col + 1));
        //NW
        neighbors.add(mover(row, col, row - 2, col - 1));
        //NE
        neighbors.add(mover(row, col, row - 1, col + 2));
        //NW
        neighbors.add(mover(row, col, row - 1, col - 2));
        //SE
        neighbors.add(mover(row, col, row + 1, col + 2));
        //SW
        neighbors.add(mover(row, col, row + 1, col - 2));
        //SE
        neighbors.add(mover(row, col, row + 2, col + 1));
        //SW
        neighbors.add(mover(row, col, row + 2, col - 1));
        return neighbors;
    }

    /**
     * helper function to find all possible successors for a bishop
     * @param row row of the cell containing the bishop piece
     * @param col col of the cell containing the bishop piece
     * @return all possible captures that can be made by the bishop
     */
    private Collection<Configuration> bNeighbor(int row, int col) {
        Collection<Configuration> neighbors = new ArrayList<>();
        boolean captured = false;
        int newRow = row;
        int newCol = col;
        while (!captured) { //NW
            newRow--;
            newCol--;
            ChessConfig toAdd = mover(row, col, newRow, newCol);
            if (toAdd != null) {
                captured = true;
                neighbors.add(toAdd);
            }
            if (newRow < 0 || newRow > rowDim || newCol < 0 || newCol > colDim){
                break;
            }
        }
        captured = false;
        newRow = row;
        newCol = col;
        while (!captured) { //NE
            newRow--;
            newCol++;
            ChessConfig toAdd = mover(row, col, newRow, newCol);
            if (toAdd != null) {
                captured = true;
                neighbors.add(toAdd);
            }
            if (newRow < 0 || newRow > rowDim || newCol < 0 || newCol > colDim){
                break;
            }
        }
        captured = false;
        newRow = row;
        newCol = col;
        while (!captured) { //SW
            newRow++;
            newCol--;
            ChessConfig toAdd = mover(row, col, newRow, newCol);
            if (toAdd != null) {
                captured = true;
                neighbors.add(toAdd);
            }
            if (newRow < 0 || newRow > rowDim || newCol < 0 || newCol > colDim){
                break;
            }
        }

        captured = false;
        newRow = row;
        newCol = col;
        while (!captured) { //SE
            newRow++;
            newCol++;
            ChessConfig toAdd = mover(row, col, newRow, newCol);
            if (toAdd != null) {
                captured = true;
                neighbors.add(toAdd);
            }
            if (newRow < 0 || newRow > rowDim || newCol < 0 || newCol > colDim){
                break;
            }
        }
        return neighbors;
    }

    /**
     * helper function to find all possible successors for a rook
     * @param row row of the cell containing the rook piece
     * @param col col of the cell containing the rook piece
     * @return all possible captures that can be made by the rook
     */
    private Collection<Configuration> rNeighbor(int row, int col) {
        Collection<Configuration> neighbors = new ArrayList<>();
        ChessConfig toAdd;
            for (int i = col-1; i >= 0; i--) { // left
                toAdd = mover(row, col, row, i);
                if (toAdd != null) {
                    neighbors.add(toAdd);
                    break;
                }
            }
            for (int j = col+1; j < colDim; j++) { // right
                toAdd = mover(row, col, row, j);
                if (toAdd != null) {
                    neighbors.add(toAdd);
                    break;
                }
            }
            for (int i = row-1; i >= 0; i--) { // top
                toAdd = mover(row, col, i, col);
                if (toAdd != null ) {
                    neighbors.add(toAdd);
                    break;
                }
            }
            for (int i = row+1; i < rowDim; i++) { // bottom
                toAdd = mover(row, col, i, col);
                if (toAdd != null) {
                    neighbors.add(toAdd);
                    break;
                }
            }
        return neighbors;
    }

    /**
     * helper function to find all possible successors for a pawn
     * @param row row of the cell containing the pawn piece
     * @param col col of the cell containing the pawn piece
     * @return all possible captures that can be made by the pawn
     */
    private Collection<Configuration> pNeighbor(int row, int col) {
        Collection<Configuration> neighbors = new ArrayList<>();
        //NW
        neighbors.add(mover(row, col, row - 1, col - 1));
        //NE
        neighbors.add(mover(row, col, row - 1, col + 1));
        return neighbors;
    }

    /**
     * Return a new config after a capture or null if the capture was invalid
     * @param row row index of the first cell
     * @param col col index of the first cell
     * @param newRow row index of the cell to be captured
     * @param newCol col index of the cell to be captured
     * @return null if invalid move; new Chess config with the capture otherwise
     */
    public ChessConfig mover(int row, int col, int newRow, int newCol) {
        try {
            if (this.board[newRow][newCol] != EMPTY) {
                ChessConfig newConfig = new ChessConfig(this);
                newConfig.board[row][col] = EMPTY;
                newConfig.board[newRow][newCol] = board[row][col];
                return newConfig;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * get the row dimensions
     * @return row dimension
     */
    public int getRowDim(){
        return rowDim;
    }

    /**
     * get the column dimensions
     * @return column dimensions
     */
    public int getColDim(){
        return colDim;
    }

    @Override
    public String toString(){
        StringBuilder entire = new StringBuilder();
        entire.append("   ");
        for (int i = 0; i < rowDim; i ++){
            entire.append(i).append(" ");
        }
        entire.append("\n   ");
        entire.append("-".repeat(Math.max(0, rowDim * 2)));
        for (int i = 0; i < rowDim; i++){
            entire.append("\n").append(i).append("| ");
            for (int j = 0; j < colDim; j++){
                entire.append(board[i][j]).append(" ");
            }
        }
        return entire.toString();
    }

    @Override
    public boolean equals(Object other){
        boolean result = false;
        if (other instanceof ChessConfig){
            ChessConfig o = (ChessConfig) other;
            result = Arrays.deepEquals(this.board, o.board);
        }
        return result;
    }

    @Override
    public int hashCode(){
        return Arrays.deepHashCode(this.board);
    }
}
