package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Triplet;
import uk.ac.soton.comp1206.event.ChangedScoresListener;
import uk.ac.soton.comp1206.event.MessageListener;
import uk.ac.soton.comp1206.network.Communicator;

/**
 * The MultiplayerGame handles the main logic, state and properties of the TetrECS Multiplayer Game.
 * This class extends Game by getting pieces from the communicator rather than randomly generating them
 */
public class MultiplayerGame extends Game {
    
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
    private Timer scoresTimer;
    private Communicator communicator;
    private Queue<GamePiece> pieceQueue;
    private SimpleListProperty<Triplet<String, String, Integer>> playersData;
    private MessageListener messageListener;
    private ChangedScoresListener scoresListener;
    private Object eventKey = new Object();
    
    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
        communicator = Communicator.getCommunicator();
        communicator.addListener(this::receiveCommunication);
    
        var tripletsArray = new ArrayList<Triplet<String, String, Integer>>();
        var observableTriplets = FXCollections.observableArrayList(tripletsArray);
        playersData = new SimpleListProperty<>(observableTriplets);
        
        pieceQueue = new LinkedList<>();
    
        //Populate the queue
        for (int i = 0; i < 5; i++) {
            requestNextPiece();
        }
    
        scoresTimer = new Timer("ScoresTimerThread");
        var task = new TimerTask() {
    
            /**
             * The action to be performed by this timer task.
             */
            @Override
            public void run() {
                requestScores();
            }
        };
        scoresTimer.scheduleAtFixedRate(task, 500, 2500);
    }
    
    /**
     * Overrides the spawnPiece method to get the next piece from the queue
     * @return the next piece in the queue
     */
    @Override
    public GamePiece spawnPiece() {
        if (pieceQueue.isEmpty()) {
            requestNextPiece();
            Platform.enterNestedEventLoop(eventKey);
        }
        return pieceQueue.remove();
    }
    
    /**
     * Replaces the current GamePiece with a new GamePiece
     * Requests the next piece from the communicator
     */
    @Override
    public void nextPiece() {
        requestNextPiece();
        super.nextPiece();
    }
    
    /**
     * Adds the given piece to the pieceQueue
     * @param stringValue the String representation of the Piece
     */
    private void queuePiece(String stringValue) {
        var value = Integer.parseInt(stringValue);
        var piece = GamePiece.createPiece(value);
        logger.info("Adding {} piece to queue", piece);
        pieceQueue.add(piece);
        try {
            Platform.exitNestedEventLoop(eventKey, null);
        } catch (IllegalArgumentException e) {
            logger.info("No nested loop");
        }
    }
    
    /**
     * logic to handle what happens after a GamePiece has been placed Clearing lines
     */
    @Override
    public void afterPiece() {
        var scoreBefore = score.get();
        super.afterPiece();
        var scoreAfter = score.get();
        if (scoreBefore != scoreAfter) {
            sendNewScore(score.getValue());
        }
    }
    
    /**
     * Method to send an update of the players score
     * @param score the new score
     */
    private void sendNewScore(Integer score) {
        communicator.send("SCORE " + score);
    }
    
    /**
     * This method handles when the timer reaches zero lose a life and current piece, timer and
     * multiplier is reset.
     */
    @Override
    protected void gameLoop() {
        super.gameLoop();
        sendNewLives(lives.getValue());
    }
    
    /**
     * Method to send an update of the players remaining lives
     * @param lives the new lives
     */
    private void sendNewLives(Integer lives) {
        communicator.send("LIVES " + lives);
    }
    
    /**
     * This method ends the game by cancelling the timer and removing all the listeners
     */
    @Override
    public void endGame() {
        logger.info("Cancelling scores timer");
        scoresTimer.cancel();
        scoresTimer = null;
        communicator.send("DIE");
        super.endGame();
    }
    
    /**
     * Method to request a new piece from the communicator
     */
    private void requestNextPiece() {
        communicator.send("PIECE");
    }
    
    /**
     * Method to request the scores of all the players in the lobby
     */
    private void requestScores() {
        communicator.send("SCORES");
    }
    
    /**
     * Method to handle all the incoming communications from the communicator
     * @param message the message from the communicator
     */
    private void receiveCommunication(String message) {
        var components = message.split(" ");
        var type = components[0];
        var content = components[1];
        switch (type) {
            case "PIECE" -> Platform.runLater(() -> queuePiece(content));
            case "MSG" -> Platform.runLater(() -> messageListener.receiveMessage(content));
            case "SCORES" -> Platform.runLater(() -> handleScores(content));
        }
    }
    
    /**
     * Method to set a listener for messages
     * @param listener the listener to set
     */
    public void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }
    
    /**
     * Method to set a listener for score updates
     * @param listener the listner to set
     */
    public void setScoresListener(ChangedScoresListener listener) {
        scoresListener = listener;
    }
    
    /**
     * Method to send MSG protocols to the communicator
     * @param message the message to send
     */
    public void sendMessage(String message) {
        communicator.send("MSG " + message);
    }
    
    /**
     * Method to handle incoming SCORES communications
     * Adds the data into the playersData List
     * @param message the message with the data
     */
    private void handleScores(String message) {
        var lines = message.split("\n");
        playersData.clear();
        for (var line : lines) {
            var data = line.split(":");
            var name = data[0];
            var score = Integer.parseInt(data[1]);
            var lives = data[2];
            playersData.add(new Triplet<>(name, lives, score));
        }
        scoresListener.updateScores();
    }
    
    /**
     * Method to expose the playersData List property
     * Enables the List to be bound
     * @return the List property
     */
    public SimpleListProperty<Triplet<String, String, Integer>> playersDataProperty() {
        return playersData;
    }
}
