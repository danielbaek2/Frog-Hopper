package puzzles.hoppers.gui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.hoppers.model.HoppersConfig;
import puzzles.hoppers.model.HoppersModel;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The GUI of the Hoppers puzzle
 *
 * @author Daniel Baek
 */
public class HoppersGUI extends Application implements Observer<HoppersModel, String> {
    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    private HoppersConfig currentConfig;
    private Label message;
    private String fileName;
    private HoppersModel model;
    private int initialRow = -1;
    private int initialCol = -1;
    private int finalRow = -1;
    private int finalCol = -1;
    private final Image redFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "red_frog.png")));
    private final Image greenFrog = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "green_frog.png")));
    private final Image lilyPad = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "lily_pad.png")));
    private final Image water = new Image(Objects.requireNonNull(getClass().getResourceAsStream(RESOURCES_DIR + "water.png")));
    private Stage stage;
    private BorderPane borderPane;
    private GridPane puzzle;
    private HBox buttons;
    private Scene scene;
    /**
     * Initializes the basis of the GUI
     */
    public void init() {
        this.fileName = getParameters().getRaw().get(0);
        try {
            this.model = new HoppersModel(this.fileName);
            this.model.addObserver(this);
            this.currentConfig = this.model.getCurrentConfig();
        } catch (IOException ignore) {
        }
    }
    /**
     * Creates the different elements of the GUI
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.borderPane = new BorderPane();
        this.message = new Label("Loaded: " + this.fileName);
        this.message.fontProperty().setValue(new Font(FONT_SIZE));
        this.borderPane.setTop(this.message);
        this.message.setAlignment(Pos.TOP_CENTER);

        this.puzzle = makePuzzle();
        this.borderPane.setCenter(this.puzzle);

        this.buttons = makeButtons();
        this.borderPane.setBottom(this.buttons);
        this.buttons.setAlignment(Pos.BOTTOM_CENTER);

        this.scene = new Scene(this.borderPane);
        this.stage.setScene(scene);
        this.stage.setTitle("Hoppers GUI");
        this.stage.show();
    }
    /**
     * Makes the bottom three buttons (LOAD, RESET, HINT)
     *
     * @return buttons
     */
    public HBox makeButtons() {
        HBox buttons = new HBox();
        Button load = new Button();
        load.setText("Load");
            load.setOnMouseClicked(event -> {
                FileChooser chooser = new FileChooser();
                String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
                currentPath += File.separator + "data" + File.separator + "hoppers";  // or "hoppers"
                chooser.setInitialDirectory(new File(currentPath));
                File file = chooser.showOpenDialog(this.stage);
                if (file != null) {
                    this.fileName = "data/hoppers/" + file.getName();
                    this.model.load(this.fileName);
                }
            });
        Button reset = new Button();
        reset.setText("Reset");
            reset.setOnMouseClicked(e -> this.model.reset(this.fileName));
        Button hint = new Button();
        hint.setText("Hint");
            hint.setOnMouseClicked(e -> this.model.hint());
        buttons.getChildren().add(load);
        buttons.getChildren().add(reset);
        buttons.getChildren().add(hint);
        return buttons;
    }
    /**
     * Makes the Hoppers puzzle with the frogs, lily-pads, and water
     *
     * @return puzzle
     */
    public GridPane makePuzzle() {
        GridPane puzzle = new GridPane();
        for (int row = 0; row < this.currentConfig.getRowDIM(); row++) {
            for (int col = 0; col < this.currentConfig.getColDIM(); col++) {
                if (this.currentConfig.getCell(row, col) == 'G') {
                    Button button = new Button();
                    button.setGraphic(new ImageView(greenFrog));
                    button.setMinSize(ICON_SIZE, ICON_SIZE);
                    button.setMaxSize(ICON_SIZE, ICON_SIZE);
                    puzzle.add(button, col, row);
                    if (this.initialRow == -1 || this.initialCol == -1) {
                        int r = row;
                        int c = col;
                        button.setOnAction(e -> assign(r, c));
                    } else {
                        this.finalRow = row;
                        this.finalCol = col;
                        button.setOnAction(e -> this.model.select(this.initialRow, this.initialCol, this.finalRow, this.finalCol));
                    }
                } else if (this.currentConfig.getCell(row, col) == 'R') {
                    Button button = new Button();
                    button.setGraphic(new ImageView(redFrog));
                    button.setMinSize(ICON_SIZE, ICON_SIZE);
                    button.setMaxSize(ICON_SIZE, ICON_SIZE);
                    puzzle.add(button, col, row);
                    if (this.initialRow == -1 || this.initialCol == -1) {
                        int r = row;
                        int c = col;
                        button.setOnAction(e -> assign(r, c));
                    } else {
                        this.finalRow = row;
                        this.finalCol = col;
                        button.setOnAction(e -> this.model.select(this.initialRow, this.initialCol, this.finalRow, this.finalCol));
                    }
                } else if (this.currentConfig.getCell(row, col) == '.') {
                    Button button = new Button();
                    button.setGraphic(new ImageView(lilyPad));
                    button.setMinSize(ICON_SIZE, ICON_SIZE);
                    button.setMaxSize(ICON_SIZE, ICON_SIZE);
                    puzzle.add(button, col, row);
                    int finalRow1 = row;
                    int finalCol1 = col;
                    button.setOnAction(e -> {
                        this.finalRow = finalRow1;
                        this.finalCol = finalCol1;
                        model.select(this.initialRow, this.initialCol, this.finalRow, this.finalCol);
                    });
                } else {
                    Button button = new Button();
                    button.setGraphic(new ImageView(water));
                    button.setMinSize(ICON_SIZE, ICON_SIZE);
                    button.setMaxSize(ICON_SIZE, ICON_SIZE);
                    puzzle.add(button, col, row);
                    int initialRow1 = row;
                    int initialCol1 = col;
                    button.setOnAction(e -> message.setText("Invalid selection (" + initialRow1 + ", " + initialCol1 + ")"));
                }
            }
        }
        return puzzle;
    }
    /**
     * Updates the GUI with updated information
     *
     * @param hoppersModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(HoppersModel hoppersModel, String msg) {
        switch (msg) {
            case "NEW PUZZLE" -> this.message.setText("Loaded: " + this.fileName);
            case "NEW CONFIG" -> {
                this.message.setText("Jumped from (" + this.initialRow + ", " + this.initialCol + ") to (" + this.finalRow + ", " + this.finalCol + ")");
                this.initialCol = -1;
                this.initialRow = -1;
                this.finalRow = -1;
                this.finalCol = -1;
            }
            case "SAME CONFIG" ->
                    this.message.setText("Can't jump from (" + this.initialRow + ", " + this.initialCol + ") to (" + this.finalRow + ", " + this.finalCol + ")");
            case "NO FILE" -> this.message.setText("File does not exist");
            case "HINT" -> this.message.setText("Hint given");
            case "RESET" -> this.message.setText("Puzzle reset");
            case "END" -> this.message.setText("NO SOLUTION");
        }
        this.currentConfig = this.model.getCurrentConfig();
        this.puzzle = makePuzzle();
        this.borderPane.setCenter(this.puzzle);
        this.puzzle.setAlignment(Pos.CENTER);
        this.buttons = makeButtons();
        this.borderPane.setBottom(this.buttons);
        this.buttons.setAlignment(Pos.BOTTOM_CENTER);
        this.borderPane.setTop(this.message);
        this.message.setAlignment(Pos.TOP_CENTER);
        this.stage.setScene(this.scene);
        this.initialRow = -1;
        this.initialCol = -1;
        this.finalCol = -1;
        this.finalRow = -1;
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
    }
    /**
     * When first frog is selected, initialRow and initialCol is assigned accordingly to the initial selection
     *
     * @param row = row of selection
     * @param col = col of selection
     */
    public void assign(int row, int col) {
        this.initialRow = row;
        this.initialCol = col;
        this.message.setText("Selected (" + row + ", " + col + ")");
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HoppersPTUI filename");
        } else {
            Application.launch(args);
        }
    }
}
