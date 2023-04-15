package uk.ac.soton.comp1206.scene;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.media.Multimedia;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Multi Player game scene. Holds the UI for the multiplayer game mode in the game.
 */
public class MultiplayerScene extends ChallengeScene {
    
    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    private MultiplayerGame game;
    private final Communicator communicator;
    private TextFlow lastMessageFlow;
    private Leaderboard leaderboard;
    
    /**
     * Create a new Multi Player game scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating the Multiplayer Scene");
        communicator = Communicator.getCommunicator();
    }
    
    /**
     * Build the Multiplayer Scene
     */
    @Override
    public void build() {
        super.build();
        
        topBar.setTitle("Battle Mode"); //change title
    
        //replace high score with a leaderboard
        sideBar.getChildren().remove(highScoreBox);
        leaderboard = new Leaderboard("Versus");
        sideBar.getChildren().add(0, leaderboard);
        
        //add chat
        String defaultMessage = "In Game Chat: Press T to send a message to Chat";
        var defaultText = new Text(defaultMessage);
        lastMessageFlow = new TextFlow(defaultText);
        lastMessageFlow.setTextAlignment(TextAlignment.CENTER);
        lastMessageFlow.getStyleClass().add("messages");
        centreBox.getChildren().add(lastMessageFlow);
        
        //Set listener to display received messages
        game.setMessageListener(this::showMessage);
        
        //Set listener to update the leaderboard when it changes
        game.setScoresListener(() -> leaderboard.update());
    
        //Bind the two player data lists to display them in the leaderboard
        leaderboard.playersScoresProperty().bind(game.playersDataProperty());
    }
    
    /**
     * Override to create a Multiplayer game
     */
    @Override
    public void setupGame() {
        logger.info("Starting a new multiplayer game");
        
        //create a new game
        super.game = new MultiplayerGame(5, 5);
        game = (MultiplayerGame) super.game;
    }
    
    /**
     * Override to check for more keyboard inputs
     * @param event KeyEvent of key pressed
     */
    @Override
    protected void keyboardInput(KeyEvent event) {
        logger.info("A key has been pressed");
        super.keyboardInput(event);
        switch (event.getCode()) {
            case T -> showMessageField();
        }
    }
    
    /**
     * overrides handleEscape to stop the scores timer to communicator
     */
    @Override
    protected void handleEscape() {
        logger.info("Escaping from the Multiplayer Scene");
        game.stopScoresTimer();
        game.endGame();
        super.handleEscape();
    }
    
    /**
     * Method to start the Scores screen once the game ends
     */
    @Override
    protected void handleEndGame() {
        Multimedia.playMusicOnce("music/end.wav");
        gameWindow.startMultiplayerScores(game);
    }
    
    /**
     * Displays a text field to enter a message in game
     * the text field auto-destructs on sending a message
     */
    private void showMessageField() {
        var messageField = new TextField();
        messageField.setId("chatField");
        messageField.setPromptText("Enter Chat Message");
        centreBox.getChildren().add(messageField);
        
        messageField.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendMessage(messageField.getText());
                centreBox.getChildren().remove(centreBox.lookup("#chatField"));
            }
        });
    }
    
    /**
     * Send the message to the communicator
     * @param message the message to send
     */
    private void sendMessage(String message) {
        game.sendMessage(message);
    }
    
    /**
     * Displays the message in the UI
     * @param message the message to show
     */
    private void showMessage(String message) {
        var pair = message.split(":", 2);
        var playerText = new Text(pair[0] + ": ");
        playerText.getStyleClass().add("playerBox");
        var messageText = new Text(pair[1]);
        messageText.getStyleClass().add("messages");
        
        lastMessageFlow.getChildren().clear();
        lastMessageFlow.getChildren().addAll(playerText, messageText);
    }
}
