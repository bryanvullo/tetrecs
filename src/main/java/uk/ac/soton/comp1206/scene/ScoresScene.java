package uk.ac.soton.comp1206.scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * This is the scene which will display all the scores
 */
public class ScoresScene extends BaseScene {
    
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private Game game;
    private SimpleListProperty<Pair<String, Integer>> localScores;
    private SimpleListProperty<Pair<String, Integer>> remoteScores;
    private String scoresFile = getClass().getResource("/scores.txt").getFile();
    private Scanner reader;
    private FileWriter writer;
    private BorderPane mainPane;
    private Comparator<Pair<String, Integer>> pairComparator = Comparator.comparingInt(Pair::getValue);
    private Comparator comparator = Collections.reverseOrder(pairComparator);
    private ScoreList scoreList;
    private ScoreList remoteList;
    private Communicator communicator = gameWindow.getCommunicator(); //TODO ask if this is okay
    private final Object KEY = new Object();
    private String name;
    
    /**
     * Creates a Scene Object
     * @param window the GameWindow to place the Scene in
     * @param game The game object at the end state
     */
    public ScoresScene(GameWindow window, Game game) {
        super(window);
        logger.info("Creating Scores Scene");
        this.game = game;
        
        //local scores
        var pairs = new ArrayList<Pair<String, Integer>>();
        var observablePairs = FXCollections.observableArrayList(pairs);
        localScores = new SimpleListProperty<>(observablePairs);
        
        //remote scores
        var remotePairs = new ArrayList<Pair<String, Integer>>();
        var observableRemotePairs = FXCollections.observableArrayList(remotePairs);
        remoteScores = new SimpleListProperty<>(observableRemotePairs);
    }
    
    /**
     * Initialise this scene. Called after creation
     * Load the scores and update the scores list and file
     *  if the game score beats any of the high scores
     */
    @Override
    public void initialise() {
        scene.setOnKeyPressed(this::keyboardInput); //keyboard input to escape scene
        communicator.addListener(this::receiveCommunication);
    
        //load scores
        loadScores();
    
        Boolean newScore = false;
        for (var pair : localScores) { //checks if game score has beaten any of the high scores
            var score = pair.getValue();
            if (game.score.get() > score) {
                displayGetNameBox();
                Platform.enterNestedEventLoop(KEY); //wait for name
                updateScores(name);
                newScore = true;
                break;
            }
        }
    
        requestRemoteScores();
        //wait for remote scores to arrive
        Platform.enterNestedEventLoop(KEY);
        
        for (var pair : remoteScores) { //checks if game score has beaten any of the online high scores
            var score = pair.getValue();
            if (game.score.get() > score) {
                if (!newScore) { //check if we've already got the name
                    displayGetNameBox();
                    Platform.enterNestedEventLoop(KEY); //wait for name
                }
                sendHiScore(name, game.score.getValue().toString()); //send hiscore to server
                //update the remote score list
                remoteScores.remove(remoteScores.size()-1);
                remoteScores.add(new Pair<>(name, game.score.getValue()));
                remoteScores.sort(comparator);
                
                break;
            }
        }
        //Update UI
        scoreList.update();
        remoteList.update();
    
        //reveal the scores
        scoreList.reveal();
        remoteList.reveal();
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
        mainPane.setCenter(centerBox);
        
        var highScoreText = new Text("High Scores");
        highScoreText.getStyleClass().add("score-text");
        centerBox.getChildren().add(highScoreText);
        
        var scoreBox = new HBox();
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setSpacing(50);
        centerBox.getChildren().add(scoreBox);
        
        scoreList = new ScoreList("Local Scores");
        scoreList.pairs.bind(localScores);
        
        remoteList = new ScoreList("Online Scores");
        remoteList.pairs.bind(remoteScores);
        scoreBox.getChildren().addAll(scoreList, remoteList);
    }
    
    /**
     * Method to get the name of the player to display in the high scores
     */
    private void displayGetNameBox() {
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
            mainPane.setCenter(scores);
            name = field.getText();
            Platform.exitNestedEventLoop(KEY, null);
        });
        
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mainPane.setCenter(scores);
                name = field.getText();
                Platform.exitNestedEventLoop(KEY, null);
            }
        });
    }
    
    /**
     * Loads the scores from a file, file name defined as a Field Variable
     */
    private void loadScores() {
        logger.info("Loading Local Scores");
        var file = new File(scoresFile);
        try {
            reader = new Scanner(file);
            if (!reader.hasNextLine()) writeDefaultScores(); //check if empty
        } catch (FileNotFoundException e) {
            logger.debug("Local Scores file not found");
        }
        
        while (reader.hasNextLine()) {
            var line = reader.nextLine();
            var pair = line.split(":");
            var user = pair[0];
            var score = Integer.parseInt(pair[1]);
            localScores.add(new Pair<>(user, score));
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
    
    /**
     * This method handles updating the local scores list and file when the player gets a new high score
     * @param playerName the name entered by the player
     */
    private void updateScores(String playerName) {
        logger.info("Updating the High Scores");
        var playerPair = new Pair(playerName, game.score.getValue());
        localScores.remove(localScores.getSize()-1); //remove the lowest score
        localScores.add(playerPair); //Add the player:score pair to the ArrayList
        localScores.sort(comparator); //sort the new list to put new pair in place
        
        writeScores(); //write new high scores to the file
    }
    
    /**
     * Send a request for the remote scores
     */
    private void requestRemoteScores() {
        logger.info("Requesting Remote High Scores");
        communicator.send("HISCORES UNIQUE");
    }
    
    /**
     * Parses the remote scores and adds them to the list property to display
     * @param message the HISCORE message from the communicator
     */
    private void loadRemoteScores(String message) {
        logger.info("Loading the Remote Scores");
        var lines = message.split("\n");
        for (int i = 0; i < 10; i++) {
            var line = lines[i];
            var pairs = line.split(":");
            var name = pairs[0];
            var score = Integer.parseInt(pairs[1]);
            remoteScores.add(new Pair<>(name, score));
        }
        remoteList.update();
        
        Platform.exitNestedEventLoop(KEY, null); //reveal the scores
    }
    
    /**
     * This method parses the messages received from the communicator
     * Calls the appropriate method to handle the message
     * @param message received from the communicator
     */
    private void receiveCommunication(String message) {
        logger.info("Received message");
        var lines  = message.split(" ");
        if (lines[0].equals("HISCORES")) {
            Platform.runLater(() -> loadRemoteScores(lines[1]));
        }
    }
    
    /**
     * Send a new HISCORE message to submit a new remote HISCORE
     * @param user the name of the local player
     * @param score the hiscore the player got
     */
    private void sendHiScore(String user, String score) {
        logger.info("Submitting remote score {}:{}", user, score);
        communicator.send("HISCORE " + user + ":" + score);
    }
}
