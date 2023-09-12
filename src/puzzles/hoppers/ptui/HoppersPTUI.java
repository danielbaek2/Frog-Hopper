package puzzles.hoppers.ptui;

import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersModel;
import java.io.IOException;
import java.util.Scanner;

/**
 * The PTUI of the Hoppers puzzle
 *
 * @author Daniel Baek
 */
public class HoppersPTUI implements Observer<HoppersModel, String> {
    private HoppersModel model;
    /** A horizontal divider */
    char HORI_DIVIDE = '-';
    /** A vertical divider */
    char VERT_DIVIDE = '|';
    private int initialRow;
    private int initialCol;
    private int finalRow;
    private int finalCol;
    private String fileName;

    /**
     * Initializes the PTUI
     *
     * @param filename = file
     */
    public void init(String filename) throws IOException {
        this.fileName = filename;
        this.model = new HoppersModel(filename);
        this.model.addObserver(this);
        System.out.println("Loaded: " + fileName);
        System.out.println(getDisplay());
        displayHelp();
    }

    /**
     * Updates the PTUI
     *
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel model, String data) {
        switch (data) {
            case "NEW CONFIG" ->
                    System.out.println("Jumped from (" + initialRow + ", " + initialCol + ") to (" + finalRow + ", " + finalCol + ")");
            case "SAME CONFIG" ->
                    System.out.println("Can't jump from (" + initialRow + ", " + initialCol + ") to (" + finalRow + ", " + finalCol + ")");
            case "NEW PUZZLE" -> System.out.println("Loaded: " + fileName);
            case "NO FILE" -> System.out.println("Failed to load: " + fileName);
            case "RESET" -> System.out.println("Puzzle reset!");
            case "HINT" -> System.out.println("Next step!");
        }
    }
    /**
     * Displays the help functions
     */
    private void displayHelp() {
        System.out.println( "h(int)              -- hint next move" );
        System.out.println( "l(oad) filename     -- load new puzzle file" );
        System.out.println( "s(elect) r c        -- select cell at r, c" );
        System.out.println( "q(uit)              -- quit the game" );
        System.out.println( "r(eset)             -- reset the current game" );
    }
    /**
     * Runs the PTUI
     */
    public void run() {
        Scanner in = new Scanner( System.in );
        for ( ; ; ) {
            System.out.print( "> " );
            String line = in.nextLine();
            String[] words = line.split( "\\s+" );
            if (words.length > 0) {
                // checks if user wants to quit puzzle
                if (words[0].startsWith( "q" )) {
                    break;
                // checks if user wants a hint
                } else if (words[0].startsWith("h")) {
                    model.hint();
                // checks if user wants to load a new puzzle
                } else if (words[0].startsWith("l")) {
                    fileName = words[1];
                    model.load(fileName);
                // checks if user wants to select a cell
                } else if (words[0].startsWith("s")) {
                    initialRow = Integer.parseInt(words[1]);
                    initialCol = Integer.parseInt(words[2]);
                    if (model.getCurrentConfig().getCell(initialRow, initialCol) == '.' || model.getCurrentConfig().getCell(initialRow, initialCol) == '*') {
                        System.out.println("No frog at (" + initialRow + ", " + initialCol + ")");
                    } else {
                        System.out.println("Selected (" + initialRow + ", " + initialCol + ")");
                        System.out.println(this.getDisplay());
                        System.out.print( "> " );
                        line = in.nextLine();
                        words = line.split( "\\s+" );
                        finalRow = Integer.parseInt(words[1]);
                        finalCol = Integer.parseInt(words[2]);
                        model.select(initialRow, initialCol, finalRow, finalCol);
                    }
                // checks if user wants to reset puzzle
                } else if (words[0].startsWith("r")) {
                    model.reset(fileName);
                }
                else {
                    displayHelp();
                }
            }
            System.out.println(getDisplay());
        }
    }
    /**
     * Creates String representation of configuration
     * @return result
     */
    public String getDisplay() {
        StringBuilder result = new StringBuilder("  ");

        for (int col=0; col<model.getCurrentConfig().getColDIM(); col++) {
            result.append(col).append(" ");
        }
        result.append(System.lineSeparator()).append("  ");

        result.append(String.valueOf(HORI_DIVIDE).repeat(Math.max(0, this.model.getCurrentConfig().getRowDIM() * 2 - 1)));
        result.append(System.lineSeparator());

        for (int row=0; row< model.getCurrentConfig().getRowDIM(); row++) {
            result.append(row);
            result.append(VERT_DIVIDE);
            for (int col = 0; col<model.getCurrentConfig().getColDIM() ; col++) {
                if (col != model.getCurrentConfig().getColDIM() -1) {
                    result.append(model.getCurrentConfig().getCell(row, col)).append(" ");
                } else {
                    result.append(model.getCurrentConfig().getCell(row, col)).append(System.lineSeparator());
                }
            }
        }
        return result.toString();
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            try {
                HoppersPTUI ptui = new HoppersPTUI();
                ptui.init(args[0]);
                ptui.run();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }
    }
}
