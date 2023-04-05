package uk.ac.soton.comp1206.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBar;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
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
    private GameBoard board;
    private Rectangle timer;
    private String scoresFile = getClass().getResource("/scores.txt").getFile();
    private Scanner reader;
    private FileWriter writer;
    
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
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);
    
        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
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
        
            //High score box
        var highScoreBox = new VBox();
        highScoreBox.setAlignment(Pos.CENTER);
        sideBar.getChildren().add(highScoreBox);
        var highScoreText = new Text("High Score");
        highScoreText.getStyleClass().add("heading");
        var highScoreField = new Text(getHighScore().toString());
        var highScore = new SimpleIntegerProperty();
        //TODO make it so highscore changes dynamically if the player beats it
        if (getHighScore() < game.score.get()) {
            highScore.bind(game.score);
        }
        highScoreField.getStyleClass().add("hiscore");
        highScoreBox.getChildren().addAll(highScoreText, highScoreField);
        
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
        
        //Bottom timer bar
        var bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        mainPane.setBottom(bottomBar);
    
        timer = new Rectangle(gameWindow.getWidth(), 25);
        bottomBar.getChildren().add(timer);
        
        //Handle when mouse hovers over a game board block
        board.setOnHover(this::handleHover);

        //Handle block on game board grid being clicked
        board.setOnBlockClick(this::blockClicked);
        
        //Handle when the game board is right-clicked
        board.setOnRightClicked(this::handleRightRotate);
        
        //Handle when the Current Piece Board is clicked
        currentPiece.setOnBlockClick(block -> handleRightRotate());
        
        //Handle when the Next Piece Board is clicked
        nextPiece.setOnBlockClick(block -> swapCurrentPieces());
        
        //Handle next Piece Event
        game.setNextPieceListener(this::handleNextPiece);
        
        //Handle Lines Cleared Event
        game.setLineClearedListener(this::handleLineCleared);
        
        //Handle the timer reset event
        game.setGameLoopListener(this::handleGameLoop);
        
        //Handle the end of the game
        game.setGameEndListener(this::handleEndGame);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        var flag = game.blockClicked(gameBlock);
        if (flag) Multimedia.playAudio("sounds/place.wav");
        else Multimedia.playAudio("sounds/fail.wav");
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
        switch (event.getCode()) {
            case ESCAPE -> handleEscape();
            case SPACE, R -> swapCurrentPieces();
            case E, C, CLOSE_BRACKET -> handleRightRotate();
            case Q, Z, OPEN_BRACKET -> handleLeftRotate();
            case W, UP -> board.moveAimedBlock(0, -1);
            case S, DOWN -> board.moveAimedBlock(0, 1);
            case A, LEFT -> board.moveAimedBlock(-1, 0);
            case D, RIGHT -> board.moveAimedBlock(1, 0);
            case ENTER -> handleEnter();
        }
    }
    
    /**
     * Handles the event when the Enter key is pressed
     */
    private void handleEnter() {
        blockClicked(board.getAimedBlock());
    }
    
    /**
     * Handles the case which the escape key has been pressed
     * Returns to the Game Menu
     */
    private void handleEscape() {
        logger.info("Escape Key have been pressed, Returning to the Menu");
        closeGame();
        gameWindow.startMenu();
    }
    
    /**
     * Handles the case where space or R has been pressed
     * Swaps the current and following Pieces
     */
    private void swapCurrentPieces() {
        logger.info("Swapping current and following pieces");
        Multimedia.playAudio("sounds/transition.wav");
        game.swapCurrentPiece();
    }
    
    /**
     * Handles the case where E, C or ] have been pressed
     * Rotates the current piece to the right
     */
    private void handleRightRotate() {
        logger.info("Rotating current piece to the right");
        Multimedia.playAudio("sounds/rotate.wav");
        game.rotateCurrentPiece();
    }
    
    /**
     * Handles the case where Q, Z or [ have been pressed
     * Rotates the current piece to the left
     */
    private void handleLeftRotate() {
        logger.info("Rotating current piece to the left");
        Multimedia.playAudio("sounds/rotate.wav");
        game.rotateCurrentPiece(3);
    }
    
    /**
     * Set the implementation for the Mouse Hover Listener
     * @param block the block that is being hovered over
     */
    private void handleHover(GameBlock block) {
        board.aimEnteredBlock(block);
    }
    
    /**
     * Method to end the game and clean up
     */
    private void closeGame() {
        //communicator.clearListeners() for multiplayer?
        game.endGame();
    }
    
    /**
     * Method to start the Scores screen once the game ends
     */
    private void handleEndGame() {
        gameWindow.startScores(game);
    }
    
    /**
     * This method handles the Next Piece Event
     * Displays the current and following game pieces in their respective boards
     * @param current The current Piece to display
     * @param following The following Piece to display
     */
    private void handleNextPiece(GamePiece current, GamePiece following) {
        logger.info("displaying current and following pieces");
        currentPiece.setPieceToDisplay(current);
        nextPiece.setPieceToDisplay(following);
    }
    
    /**
     * This method handles the Line Cleared Event
     * takes a set of GameBlockCoordinates and called the fadeOut method on it
     * @param coordinates the coordinates of the blocks to fade out
     */
    private void handleLineCleared(GameBlockCoordinate[] coordinates) {
        logger.info("DEBUG handling line cleared");
        board.fadeOut(coordinates);
    }
    
    /**
     * This method handles the time bar animation
     * @param time the duration of the current timer delay
     */
    private void handleGameLoop(int time) {
        var animation = new Transition() {
            {
                setCycleDuration(Duration.millis(time));
                interpolatorProperty().set(Interpolator.LINEAR);
            }
            @Override
            protected void interpolate(double frac) {
                frac = (float) frac;
                timer.widthProperty().set(gameWindow.getWidth() * (1-frac));
                var colour = Color.color(1 * frac,1 * (1-frac),0);
                timer.fillProperty().set(colour);
            }
        };
        animation.play();
    }
    
    /**
     * gets the High Score from the scores file
     * @return the high score Integer
     */
    private Integer getHighScore() {
        logger.info("Getting High Score from Local Scores File");
        var file = new File(scoresFile);
        try {
            reader = new Scanner(file);
            if (!reader.hasNextLine()) writeDefaultScores();
        } catch (FileNotFoundException e) {
            logger.debug("Local Scores file not found");
        }
        var topScore = reader.nextLine();
        var pair = topScore.split(":");
        var score = Integer.parseInt(pair[1]);
        return score;
    }
    
    /**
     * Write default scores into a file to populate it initially
     */
    private void writeDefaultScores() {
        logger.info("Writing Default Scores");
        var file = new File(scoresFile);
        String data = "";
        for (int scoreCount = 10; scoreCount > 0; scoreCount--) {
            data = data + "Bryan:" + (scoreCount * 1000) + "\n";
        }
        //write default data in the file
        try {
            file.createNewFile();
            writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            logger.debug("Unable to write default data to score file");
        }
    }
}
