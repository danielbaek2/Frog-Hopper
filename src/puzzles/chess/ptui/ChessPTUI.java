package puzzles.chess.ptui;

import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * A Plain-Test User Interface for the Chess puzzle
 * @author Erica Wu <ew3797>
 */
public class ChessPTUI implements Observer<ChessModel, String> {
    /** view/controller access to the model */
    private ChessModel model;

    /**
     * create the chess model and register this object as an observer
     * of it
     * @param filename
     * @throws IOException
     */
    public void init(String filename) throws IOException {
        this.model = new ChessModel(filename);
        this.model.addObserver(this);
        this.model.load(new File(filename));
        System.out.println();
        displayHelp();
    }

    /**
     * the model has some changes
     * query the model to find and display the current state
     * of the board
     * Print the board and the state of the game
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel model, String data) {
        final ChessModel.GameState gameState = model.gameState();
        if (gameState != ChessModel.GameState.ONGOING){
            System.out.println(data);
            System.out.println(model.get());
            System.out.println();
        }
    }

    /**
     * display the help menu
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }

    /**
     * detects user input
     * @throws IOException if there is a problem reading a new file when loading
     */
    public void run() throws IOException {
        Scanner in = new Scanner(System.in);
        for (; ; ) {
            System.out.print("> ");
            String line = in.nextLine();
            String[] words = line.split("\\s+");
            if (words.length > 0) {
                if (words[0].startsWith("q")) {
                    break;
                } else if (words[0].equals("l") || words[0].equals("load")) {
                    this.model.load(new File(words[1]));
                } else if (words[0].equals("s") || words[0].equals("select")) {
                    this.model.select(Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                } else if (words[0].equals("r") || words[0].equals("reset")) {
                    this.model.reset();
                } else if (words[0].equals("h") || words[0].equals("hint")) {
                    this.model.hint();
                } else {
                    displayHelp();
                }
            }
        }
    }

    /**
     * start up the ptui
     * @param args file to use as the first config
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        } else {
            try {
                ChessPTUI ptui = new ChessPTUI();
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
