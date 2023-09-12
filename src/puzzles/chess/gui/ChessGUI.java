package puzzles.chess.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.chess.model.ChessConfig;
import puzzles.common.Observer;
import puzzles.chess.model.ChessModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * The graphical user interface to the chess puzzle
 * @author Erica Wu <ew3797>
 */

public class ChessGUI extends Application implements Observer<ChessModel, String> {
    /** chess puzzle model */
    private ChessModel model;
    /** config of the model */
    private ChessConfig config;

    /** The size of all icons, in square dimension */
    private final static int ICON_SIZE = 75;
    /** the font size for labels and buttons */
    private final static int FONT_SIZE = 12;

    /** stage of the GUI */
    private Stage stage;
    /** borderpane for the to set the scene on the stage */
    private BorderPane borderPane;

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    /** images of the pieces for the button images*/
    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));



    /** a definition of light and dark and for the button backgrounds */
    private static final Background LIGHT =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    private static final Background DARK =
            new Background( new BackgroundFill(Color.MIDNIGHTBLUE, null, null));

    /**
     * create the chess model and register this object as an observer of it
     * @throws IOException if there was an issue reading the file
     */
    @Override
    public void init() throws IOException {
        // get the file name from the command line
        String filename = getParameters().getRaw().get(0);
        this.model = new ChessModel(filename);
        this.model.addObserver(this);

        this.config = this.model.get();
    }

    /**
     * set up the GUI
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setTitle("Chess GUI");


        this.borderPane = new BorderPane();
        Label label = new Label("Loaded: " + model.file.getName());
        label.fontProperty().setValue(new Font(FONT_SIZE));
        borderPane.setTop(label);
        borderPane.setCenter(makeBoard());
        borderPane.setBottom(makeButtons());

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * the model has some changes
     * query the model to find and display the current state of the game
     * @param chessModel the object that wishes to inform this object
     *                about something that has happened.
     * @param msg optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel chessModel, String msg) {
        this.stage.sizeToScene();  // when a different sized puzzle is loaded
        this.config = model.get();
        Label label = new Label(msg);
        label.fontProperty().setValue(new Font(FONT_SIZE));
        borderPane.setTop(label);
        borderPane.setCenter(makeBoard());

    }

    /**
     * helper function to make the chess board
     * @return gridpane of buttons representing the chess board
     */
    private GridPane makeBoard(){
        GridPane gridPane = new GridPane();
        for (int r = 0; r < config.getRowDim(); r++){
            for (int c = 0; c < config.getColDim(); c++){
                char current = this.config.getCell(r,c);
                Button cell = new Button();
                if (current == ChessConfig.KING){
                    cell.setGraphic(new ImageView(king));
                }else if (current == ChessConfig.QUEEN){
                    cell.setGraphic(new ImageView(queen));
                }else if (current == ChessConfig.ROOK){
                    cell.setGraphic(new ImageView(rook));
                }else if (current == ChessConfig.KNIGHT){
                    cell.setGraphic(new ImageView(knight));
                }else if (current == ChessConfig.BISHOP){
                    cell.setGraphic(new ImageView(bishop));
                }else if (current == ChessConfig.PAWN){
                    cell.setGraphic(new ImageView(pawn));
                }
                if (r % 2 == 0){
                    if (c % 2 == 0){
                        cell.setBackground(LIGHT);
                    }else{
                        cell.setBackground(DARK);
                    }
                }else{
                    if (c % 2 == 0){
                        cell.setBackground(DARK);
                    }else{
                        cell.setBackground(LIGHT);
                    }
                }
                cell.setMinSize(ICON_SIZE, ICON_SIZE);
                cell.setMaxSize(ICON_SIZE, ICON_SIZE);


                cell.setOnMouseClicked(event -> {
                    int cRow = GridPane.getRowIndex(cell);
                    int cCol = GridPane.getColumnIndex(cell);
                    this.model.select(cRow, cCol);
                });

                gridPane.add(cell, c , r);
            }
        }
        return gridPane;
    }

    /**
     * helper function to make the control buttons
     * @return horizontal box containing the control buttons
     */
    private HBox makeButtons(){
        HBox hBox = new HBox();
        Button load = new Button("Load");
        load.setOnMouseClicked(event -> {
            FileChooser chooser = new FileChooser();
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            currentPath += File.separator + "data" + File.separator + "chess";  // or "hoppers"
            chooser.setInitialDirectory(new File(currentPath));
            File file = chooser.showOpenDialog(stage);
            try {
                if (file != null) {
                    this.model.load(file);
                    this.config = this.model.get();

                    this.borderPane = new BorderPane();
                    Label label = new Label();
                    label.setText("Loaded: " + model.file.getName());
                    borderPane.setTop(label);
                    this.borderPane.setCenter(makeBoard());
                    this.borderPane.setBottom(makeButtons());
                    Scene scene = new Scene(borderPane);
                    this.stage.setScene(scene);

                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        Button reset = new Button("Reset");
        reset.setOnMouseClicked(event -> {
            try {
                this.model.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button hint = new Button("Hint");
        hint.setOnMouseClicked(event -> {
            try {
                this.model.hint();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        hBox.getChildren().add(load);
        hBox.getChildren().add(reset);
        hBox.getChildren().add(hint);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    /**
     * start up the applciation
     * @param args the file to start as the first config
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
