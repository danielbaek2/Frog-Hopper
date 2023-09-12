package puzzles.chess.model;

import puzzles.common.Observer;
import puzzles.common.solver.Solver;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * The model for the chess puzzle
 * @author Erica Wu <ew3797>
 */
public class ChessModel {
    /** possible game state */
    public enum GameState {ONGOING, WON, LOST, ILLEGAL, SELECTED, CAPTURED, LOADED, RESET, HINT, LOAD_FAIL}

    /**
     * the collection of observers of this model
     */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /**
     * the current configuration
     */
    private ChessConfig currentConfig;

    /** (row,col) indexes for selection */
    private int row1, col1, row2, col2;
    /** source file to be read */
    public File file;
    /** game's current state */
    private GameState gameState;

    private static final EnumMap<GameState, String> STATE_MSGS =
            new EnumMap<>(Map.of(
                    GameState.WON, "Already solved!",
                    GameState.LOST, "No solution",
                    GameState.ONGOING, "Make a move!",
                    GameState.SELECTED, "Selected",
                    GameState.CAPTURED, "Captured from ",
                    GameState.LOADED, "Loaded: ",
                    GameState.RESET, "Puzzle reset!",
                    GameState.ILLEGAL, "Invalid",
                    GameState.HINT, "Next step!",
                    GameState.LOAD_FAIL, "Failed to load: "
            ));

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /**
     * takes the file, and creates a new configuration
     * the row/col indexes for selection are initialized
     * @param filename
     * @throws IOException
     */
    public ChessModel(String filename) throws IOException {
        this.currentConfig = new ChessConfig(filename);
        this.file = new File(filename);
        row1 = -1;
        col1 = -1;
        row2 = -1;
        col2 = -1;
    }

    /**
     *
     * @param filename file to be read
     * @throws IOException if there is an issue reading the file
     */
    public void load(File filename) throws IOException {
        if (filename.getName().contains("chess")) {
            this.gameState = GameState.LOADED;
            this.file = filename;
            this.currentConfig = new ChessConfig(filename.getPath());
            row1 = -1;
            col1 = -1;
            row2 = -1;
            col2 = -1;
            this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState) +
                    filename.getName());
            this.gameState = GameState.ONGOING;
        }else{
            this.gameState = GameState.LOAD_FAIL;
            this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState) +
                    filename);
        }
    }

    /**
     * resets the board
     * @throws IOException if there is an issue reading the file
     */
    public void reset() throws IOException {
        this.gameState = GameState.RESET;
        this.currentConfig = new ChessConfig(file.getPath());
        col1 = -1;
        row2 = -1;
        col2 = -1;
        this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState));
        this.gameState = GameState.ONGOING;
    }

    /**
     *  if the current state of the puzzle is solvable, the puzzle should
     *  advance to the next step in the solution with an indication that
     *  it was successful. Otherwise, the puzzle should remain in the same
     *  state and indicate there is no solution.
     */
    public void hint() throws IOException {
        if (this.currentConfig.isSolution()){
            this.gameState = GameState.WON;
            this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState));
        }else {
            this.gameState = GameState.HINT;
            Solver chess = new Solver(currentConfig);
            chess.solve();
            if (chess.getPath().size() < 1){
                this.gameState = GameState.LOST;
                this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState));
            }
            else{
                currentConfig = (ChessConfig) chess.getPath().get(1);
                this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState));
                this.gameState = GameState.ONGOING;
            }
        }
    }

    /**
     * For the first selection, the user should be able to select a cell
     * on the board with the intention of selecting the piece at that
     * location. If there is a piece there, there should be an indication
     * and selection should advance to the second part. Otherwise if
     * there is no piece there an error message should be displayed and
     * selection has ended.
     * For the second selection, the user should be able to select
     * another cell on the board with the intention of moving the
     * previously selected piece to this location. If the move is valid,
     * it should be made and the board should be updated and with an
     * appropriate indication. If the move is invalid, and error message
     * should be displayed.
     * @param row row index of cell being selected
     * @param col col index of the cell being selected
     */
    public void select(int row, int col) {
        if (this.gameState != GameState.WON) {
            if (row < 0 || col < 0 || row >= ChessConfig.rowDim || col >= ChessConfig.colDim
                    || currentConfig.getCell(row, col) == ChessConfig.EMPTY) {
                this.gameState = GameState.ILLEGAL;
                row1 = -1;
                col1 = -1;
                row2 = -1;
                col2 = -1;
                this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState) + " selection (" + row + ", " + col + ")");
                this.gameState = GameState.ONGOING;
            } else {
                if (row1 == -1) {
                    row1 = row;
                    col1 = col;
                    this.gameState = GameState.SELECTED;
                    this.alertObservers(ChessModel.STATE_MSGS.get(this.gameState)+
                            "(" + row1 + ", " + col1 + ")");
                    this.gameState = GameState.ONGOING;
                } else {
                    row2 = row;
                    col2 = col;
                    move(row1, col1, row2, col2);
                }
            }
        }
    }

    /**
     * get the current gameState
     * @return current gameState
     */
    public GameState gameState(){
        return this.gameState;
    }

    /** get the current config
     * @return the current config
     */
    public ChessConfig get(){
        return this.currentConfig;
    }

    /**
     * the second part of selection where the move is valid
     * and the board is to updated
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     */
    private void move(int r1, int c1, int r2, int c2) {
        char current = currentConfig.getCell(r1, c1);
        if (r1 == r2 && c1 == c2){
            invalidCapture(r1, c1, r2, c2);
        } else if (current == ChessConfig.KING && kMove(r1, c1, r2, c2)) {
            capture(r1, c1, r2, c2);
        } else if (current == ChessConfig.QUEEN && qMove(1, c1, r2, c2)) {
            capture(r1, c1, r2, c2);
        } else if (current == ChessConfig.ROOK && rMove(r1, c1, r2, c2)) {
            capture(r1, c1, r2, c2);
        } else if (current == ChessConfig.KNIGHT && knMove(r1, c1, r2, c2)) {
            capture(r1, c1, r2, c2);
        } else if (current == ChessConfig.BISHOP && bMove(r1, c1, r2, c2)) {
            capture(r1, c1, r2, c2);
        } else if (current == ChessConfig.PAWN && pMove(r1, c1, r2, c2) ) {
            capture(r1, c1, r2, c2);
        } else{
            invalidCapture(r1, c1, r2, c2);
        }
        this.gameState = GameState.ONGOING;
    }

    /**
     * move helper function to determine whether the second selection
     * is a valid move if the first selection is a king
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     * @return true if valid; false otherwise
     */
    private boolean kMove(int r1, int c1, int r2, int c2) {
        if (r2 == r1 - 1) {
            return c2 <= c1 + 1 && c2 >= c1 - 1;
        } else if (r2 == r1) {
            return c2 <= c1 + 1 && c2 >= c1 - 1;
        } else if (r2 == r1 + 1) {
            return c2 <= c1 + 1 && c2 >= c1 - 1;
        }
        return false;
    }

    /**
     * move helper function to determine whether the second selection
     * is a valid move if the first selection is a queen
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     * @return true if valid; false otherwise
     */
    private boolean qMove(int r1, int c1, int r2, int c2) {
        return bMove(r1, c1, r2, c2) || rMove(r1, c1, r2, c2);
    }

    /**
     * move helper function to determine whether the second selection
     * is a valid move if the first selection is a rook
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     * @return true if valid; false otherwise
     */
    private boolean rMove(int r1, int c1, int r2, int c2) {
        if (r1 == r2){
            if (c2 < c1){
                for(int i = c1 -1; i > c2; i--){
                    if(this.currentConfig.getCell(r1, i) != ChessConfig.EMPTY){
                        return false;
                    }
                } return true;
            } else if (c2 > c1) {
                for(int i = c1 + 1; i < c2; i++){
                    if(this.currentConfig.getCell(r1, i) != ChessConfig.EMPTY){
                        return false;
                    }
                } return true;
            }
            else{
                return false;
            }
        } else if (c1 == c2) {
            if (r2 < r1){
                for(int i = r1 -1; i > r2; i--){
                    if(this.currentConfig.getCell(i, c1) != ChessConfig.EMPTY){
                        return false;
                    }
                } return true;
            } else if (r2 > r1) {
                for(int i = c1 + 1; i < c2; i++){
                    if(this.currentConfig.getCell(i, c1) != ChessConfig.EMPTY){
                        return false;
                    }
                } return true;
            }
            else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * move helper function to determine whether the second selection
     * is a valid move if the first selection is a knight
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     * @return true if valid; false otherwise
     */
    private boolean knMove(int r1, int c1, int r2, int c2) {
        if (r2 == r1 - 2){
            return c2 == c1 - 1 || c2 == c1 + 1;
        } else if (r2 == r1 - 1){
            return c2 == c1 - 2 || c2 == c1 + 2;
        } else if ( r2 == r1 + 1){
            return c2 == c1 - 2 || c2 == c1 + 2;
        } else if (r2 == r1 + 2){
            return c2 == c1 - 1 || c2 == c1 + 1;
        } return false;
    }

    /**
     * move helper function to determine whether the second selection
     * is a valid move if the first selection is a bishop
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     * @return true if valid; false otherwise
     */
    private boolean bMove(int r1, int c1, int r2, int c2) {
        if (abs(c2 - c1) != abs(r2 - r1)){
            return false;
        }else if(r2 < r1 && c2 < c1){
            for (int i = r1 - 1; i > r2; i--){
                for (int j = c1 - 1; j > c2; j--){
                    if (this.currentConfig.getCell(i, j) != ChessConfig.EMPTY){
                        return false;
                    }
                }
            }
            return true;
        }else if(r2 < r1 && c2 > c1){
            for (int i = r1 - 1; i > r2; i--){
                for (int j = c1 + 1; j < c2; j++){
                    if (this.currentConfig.getCell(i, j) != ChessConfig.EMPTY){
                        return false;
                    }
                }
            }
            return true;
        }else if(r2 > r1 && c2 < c1){
            for (int i = r1 + 1; i < r2; i++){
                for (int j = c1 - 1; j > c2; j--){
                    if (this.currentConfig.getCell(i, j) != ChessConfig.EMPTY){
                        return false;
                    }
                }
            }
            return true;
        }else if(r2 > r1 &&  c2 > c1){
            for (int i = r1 + 1; i < r2; i++){
                for (int j = c1 + 1; j < c2; j++){
                    if (this.currentConfig.getCell(i, j) != ChessConfig.EMPTY){
                        return false;
                    }
                }
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * move helper function to determine whether the second selection
     * is a valid move if the first selection is a pawn
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     * @return true if valid; false otherwise
     */
    private boolean pMove(int r1, int c1, int r2, int c2) {
        return r2 == r1 - 1 && c2 != c1 && c2 <= c1 + 1 && c2 >= c1 - 1;
    }

    /**
     * move helper function to capture and update the board and state
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     */
    private void capture(int r1, int c1, int r2, int c2){
        this.gameState = GameState.CAPTURED;
        this.currentConfig = this.currentConfig.mover(r1, c1, r2, c2);
        row1 = -1;
        col1 = -1;
        row2 = -1;
        col2 = -1;
        alertObservers(ChessModel.STATE_MSGS.get(this.gameState) + "(" + r1 + ", " + c1 +
                ") to (" + r2 + ", " + c2 + ")");
    }

    /** helper function for when the capture is invalid and state
     * @param r1 row index of the first selection
     * @param c1 col index of the first selection
     * @param r2 row index of the second selection
     * @param c2 col index of the second selection
     */
    private void invalidCapture(int r1, int c1, int r2, int c2){
        this.gameState = GameState.ILLEGAL;
        row1 = -1;
        col1 = -1;
        row2 = -1;
        col2 = -1;
        alertObservers(ChessModel.STATE_MSGS.get(this.gameState) +" - Can't capture from (" + r1 + ", " + c1 +
                ") to (" + r2 + ", " + c2 + ")");
    }
}