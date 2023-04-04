package uk.ac.soton.comp1206.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * This is the scene which will display all the scores
 */
public class ScoresScene extends BaseScene {
    
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private Game game;
    private SimpleListProperty<Pair<String, Integer>> localScores;
    private String scoresFile = getClass().getResource("/scores.txt").getFile();
    private Scanner reader;
    private FileWriter writer;
    private BorderPane mainPane;
    private Comparator<Pair<String, Integer>> pairComparator = Comparator.comparingInt(Pair::getValue);
    private Comparator comparator = Collections.reverseOrder(pairComparator);
    private String playerName;
    private ScoreList scoreList;
    
    /**
     * Creates a Scene Object
     * @param window the GameWindow to place the Scene in
     * @param game The game object at the end state
     */
    public ScoresScene(GameWindow window, Game game) {
        super(window);
        logger.info("Creating Scores Scene");
        this.game = game;
        var pairs = new ArrayList<Pair<String, Integer>>();
        var observablePairs = FXCollections.observableArrayList(pairs);
        localScores = new SimpleListProperty<>(observablePairs);
    }
    
    /**
     * Initialise this scene. Called after creation
     * Load the scores and update the scores list and file
     *  if the game score beats any of the high scores
     */
    @Override
    public void initialise() {
        scene.setOnKeyPressed(this::keyboardInput); //keyboard input to escape scene
        
        //loads scores
        loadScores();
        for (var pair : localScores) { //checks if game score has beaten any of the high scores
            var score = pair.getValue();
            if (game.score.get() > score) { //removes the last score, adds the new score and re-sorts list
                getPlayerName();
                var playerPair = new Pair(playerName, game.score.getValue());
                localScores.remove(localScores.getSize()-1);
                localScores.add(playerPair);
                localScores.sort(comparator);
                writeScores(); //write new high scores to the file
                break;
            }
        }
        scoreList.update(); //update the scores list
        scoreList.reveal(); //animate
    }
    
    /**
     * Build the layout of the scores scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
    
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
    
        var scorePane = new StackPane();
        scorePane.setMaxWidth(gameWindow.getWidth());
        scorePane.setMaxHeight(gameWindow.getHeight());
        scorePane.getStyleClass().add("challenge-background");
        root.getChildren().add(scorePane);
    
        mainPane = new BorderPane();
        scorePane.getChildren().add(mainPane);
        
        var topBar = new VBox();
        topBar.setAlignment(Pos.CENTER);
        var gameTitleUri = getClass().getResource("/images/TetrECS.png").toExternalForm();
        var gameTitle = new ImageView(gameTitleUri);
        gameTitle.setPreserveRatio(true);
        gameTitle.setFitWidth(gameWindow.getWidth()/1.3);
        var gameOverText = new Text("Game Over");
        gameOverText.getStyleClass().add("game-over");
        topBar.getChildren().addAll(gameTitle, gameOverText);
        topBar.setPadding(new Insets(50, 0, 10, 0));
        mainPane.setTop(topBar);
    
        var centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        var highScoreText = new Text("High Scores");
        highScoreText.getStyleClass().add("score-text");
        mainPane.setCenter(centerBox);
        scoreList = new ScoreList("Local Scores");
        scoreList.pairs.bind(localScores);
        centerBox.getChildren().addAll(highScoreText, scoreList);
    }
    
    /**
     * Method to get the name of the player to display in the high scores
     */
    private void getPlayerName() {
        var scores = mainPane.getCenter(); //temporarily hold this node to display something else
    
        var newScoreBox = new VBox();
        newScoreBox.setAlignment(Pos.CENTER);
        newScoreBox.setSpacing(20);
        mainPane.setCenter(newScoreBox);
        var field = new TextField();
        field.setMaxWidth(gameWindow.getWidth()/1.3);
        field.setPromptText("Enter Your Name");
        var button = new Button("Submit");
        var text = new Text("You got a High Score!");
        text.getStyleClass().add("score-text");
        newScoreBox.getChildren().addAll(text, field, button);
        
        //button action: set name and restore scores node at center
        button.setOnAction((event) -> {
           playerName = field.getText();
           mainPane.setCenter(scores);
        });
    }
    
    /**
     * Loads the scores from a file, file name defined as a Field Variable
     */
    private void loadScores() {
        logger.info("Loading Local Scores");
        var file = new File(scoresFile);
        if (!file.exists()) writeDefaultScores();
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e) {
            logger.debug("Local Scores file not found");
        }
        int counter = 0;
        while (reader.hasNextLine() & counter < 10) {
            var line = reader.nextLine();
            var pair = line.split(":");
            var user = pair[0];
            var score = Integer.parseInt(pair[1]);
            localScores.add(counter, new Pair<>(user, score));
            counter++;
        }
    }
    
    /**
     * Write the scores into the file
     */
    private void writeScores() {
        logger.info("Writing Scores");
        var file = new File(scoresFile);
        String data = "";
        
        //creating data string to write
        for (var pair : localScores) {
            var user = pair.getKey();
            var score = pair.getValue();
            data = data + user + ":" + score + "\n";
        }
    
        //write the data into the file
        try {
            writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            logger.debug("Unable to write to file");
        }
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
    
    /**
     * Handles the event which the user pressed a key
     * @param event KeyEvent of key pressed
     */
    private void keyboardInput(KeyEvent event) {
        logger.info("A key has been Pressed");
        if (event.getCode() != KeyCode.ESCAPE) return;
        handleEscape();
    }
    
    /**
     * Handles the case which the escape key has been pressed
     * Returns to the Game Menu
     */
    private void handleEscape() {
        logger.info("Escape Key have been pressed, Returning to the Menu from Scores Scene");
        gameWindow.startMenu();
    }
}
