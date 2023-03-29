package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBar;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.media.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * Game model of the game to link to UI
     */
    protected Game game;
    private PieceBoard currentPiece;
    private PieceBoard nextPiece;
    
    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        Multimedia.playMusic("music/game_start.wav"); //background music: play once
        Multimedia.setOnMusicEnd("music/game.wav"); //background music: loop this track for game
        
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);
        
        //adding UI components: score, lives, level and multiplier
        var topBar = new GameBar("Challenge Mode");
        //binding the UI properties to the model
        topBar.scoreProperty().bind(game.score.asString());
        topBar.livesProperty().bind(game.lives.asString());
        topBar.levelProperty().bind(game.level.asString());
        topBar.multiplierProperty().bind(game.multiplier.asString());
        
        mainPane.setTop(topBar);
    
        //adding sidebar UI components
        var sideBar = new VBox();
        sideBar.alignmentProperty().set(Pos.CENTER);
        mainPane.setRight(sideBar);
        
        //current piece board
        var currentPieceText = new Text("Current Piece");
        currentPieceText.getStyleClass().add("heading");
        currentPiece = new PieceBoard(new Grid(3,3),
            gameWindow.getWidth()/4, gameWindow.getWidth()/4);
        sideBar.getChildren().addAll(currentPieceText, currentPiece);
        //next piece board
        var nextPieceText = new Text("Next Piece");
        nextPieceText.getStyleClass().add("heading");
        nextPiece = new PieceBoard(new Grid(3,3),
            gameWindow.getWidth()/8, gameWindow.getWidth()/8);
        sideBar.getChildren().addAll(nextPieceText, nextPiece);

        //Handle block on game board grid being clicked
        board.setOnBlockClick(this::blockClicked);
        
        //Handle next Piece Event
        game.setNextPieceListener(this::handleNextPiece);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Set up the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        scene.setOnKeyPressed(this::keyboardInput);
        game.start();
    }
    
    /**
     * Handles the event which the user pressed a key
     * @param event KeyEvent of key pressed
     */
    private void keyboardInput(KeyEvent event) {
        logger.info("A key has been Pressed");
        if (event.getCode() == KeyCode.ESCAPE) {
            handleEscape();
        }
    }
    
    /**
     * Handles the case which the escape key has been pressed
     * Returns to the Game Menu
     */
    private void handleEscape() {
        logger.info("Escape Key have been pressed, Returning to the Menu");
        endGame();
        gameWindow.startMenu();
    }
    
    /**
     * Method to end the game and clean up
     */
    private void endGame() {
        //TODO: stop game timer and listeners. then open scores
        //communicator.clearListeners() for multiplayer
    }
    
    private void handleNextPiece(GamePiece piece) {
        logger.info("displaying current piece");
        currentPiece.setPieceToDisplay(nextPiece.getPiece());
        nextPiece.setPieceToDisplay(piece);
    }

}
