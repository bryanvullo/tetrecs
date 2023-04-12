package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.media.Multimedia;
import uk.ac.soton.comp1206.network.Communicator;

/**
 * This custom component is used to display the Lobby Chat of the game
 * when a player is connected to a game
 */
public class LobbyChat extends VBox {
    
    private static final Logger logger = LogManager.getLogger(LobbyChat.class);
    private Timer usersTimer;
    private Communicator communicator;
    private StringProperty nickname;
    private List<String> users;
    private BooleanProperty disabledHost;
    private VBox messages;
    private Boolean scrollToBottom = false;
    private ScrollPane scroll;
    private TextFlow usersFlow;
    
    /**
     * Creates a LobbyChat Component
     */
    public LobbyChat(Communicator communicator, Integer height) {
        logger.info("Creating a LobbyChat");
        setPrefHeight(height);
        this.communicator = communicator;
    
        usersTimer = new Timer("UsersTimerThread");
        var task = new TimerTask() {
            /**
             * The action to be performed by this timer task.
             */
            @Override
            public void run() {
                requestUsers();
            }
        };
        usersTimer.scheduleAtFixedRate(task, 500, 5000);
        
        disabledHost = new SimpleBooleanProperty(true);
        users = new ArrayList<String>();
        build();
    }
    
    /**
     * Builds the UI of the LobbyChat
     */
    private void build() {
        logger.info("Building a LobbyChat");
        setStyle("-fx-padding: 10; -fx-border-color: white; -fx-border-style: solid inside; "
            + "-fx-border-width: 4; -fx-border-radius: 5; -fx-border-insets: 5");
        setSpacing(10);
    
        usersFlow = new TextFlow();
        usersFlow.getStyleClass().add("messages");
        
        messages = new VBox();
        messages.getStyleClass().add("messages");
        var welcomeText = new Text("Welcome to the lobby \n Type /nick NewName to change your name \n");
        messages.getChildren().add(welcomeText);
        
        scroll = new ScrollPane();
        scroll.getStyleClass().add("scroller");
        setVgrow(scroll, Priority.ALWAYS);
        scroll.setContent(messages);
        scroll.setFitToWidth(true);
        
        var messageField = new TextField();
        var startButton = new Button("Start");
        var leaveButton = new Button("Leave");
        var bottomBar = new BorderPane();
        bottomBar.setLeft(startButton);
        bottomBar.setRight(leaveButton);
        
        getChildren().addAll(usersFlow, scroll, messageField, bottomBar);
        
        startButton.disableProperty().bind(disabledHost);
        startButton.setOnAction(this::startGame);
        leaveButton.setOnAction(this::leave);
        messageField.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage(messageField.getText());
                messageField.clear();
            }
        });
    }
    
    /**
     * Move the scroller to the bottom
     */
    public void jumpToBottom() {
        if(!scrollToBottom) return;
        scroll.setVvalue(1.0f);
        scrollToBottom = false;
    }
    
    /**
     * Method to enable the host functionality of the lobby chat
     * the host can start the game, the start game button is enabled
     * @param host whether the play is the host
     */
    public void setHost(Boolean host) {
        logger.info("Setting host to {}", host);
        this.disabledHost.set(!host);
    }
    
    /**
     * Handles the send-message action from text field
     * if message starts with /nick then change player name
     * any other case, the message is sent to the communicator
     * @param message the message to send
     */
    private void sendMessage(String message) {
        if (message.startsWith("/nick")) {
            var name = message.replaceFirst("/nick ", "");
            logger.info("Changing nickname to {}", name);
            communicator.send("NICK " + name);
        } else {
            logger.info("Sending message {}", message);
            communicator.send("MSG " + message);
        }
    
    }
    
    /**
     * Request the list of users in the Lobby
     */
    private void requestUsers() {
        logger.info("Requesting list of users in lobby");
        communicator.send("USERS");
    }
    
    /**
     * Handles when we receive a MSG from communicator
     * @param communication the content of the message
     */
    public void receiveMessage(String communication) {
        logger.info("Received a message, displaying it to the chat UI");
        Multimedia.playAudio("sounds/message.wav");
        
        var pair = communication.split(":", 2);
        var user = pair[0];
        var content = pair[1];
        
        var time = java.time.LocalTime.now();
        var timeStamp = new Text("["+ time.getHour() + ":" + time.getMinute() + "] ");
        var userText = new Text(user + ": ");
        userText.getStyleClass().add("playerBox");
        if (user.equals(nickname.get())) {
            userText.getStyleClass().add("myname");
        }
        var messageText = new Text(content);
        messageText.getStyleClass().add("messages");
        
        var message = new TextFlow(timeStamp, userText, messageText);
        
        messages.getChildren().add(message);
    
        if(scroll.getVvalue() == 0.0f || scroll.getVvalue() > 0.9f) {
            scrollToBottom = true;
        }
    }
    
    /**
     * Leave the lobby, sets host to false
     * @param event event from button
     */
    private void leave(ActionEvent event) {
        logger.info("Leaving the lobby");
        communicator.send("PART");
        disabledHost.set(true);
    }
    
    /**
     * Start the game for everyone in the Lobby
     * @param event event from button
     */
    private void startGame(ActionEvent event) {
        logger.info("Starting the game from everyone in the lobby");
        communicator.send("START");
    }
    
    /**
     * Handles when the player changes their name
     * @param nickname the new nickname
     */
    public void setNickname(String nickname) {
        logger.info("Changing local player's nickname to {}", nickname);
        this.nickname = new SimpleStringProperty(nickname);
    }
    
    /**
     * Handles NICK messages from communicator
     * only cares about if the local player changed their name
     * @param message the content of the NICK communication
     */
    public void handleChangeName(String message) {
        logger.info("Handling NICK messages from communicator");
        if (!message.contains(":")) {
            setNickname(message);
        }
    }
    
    /**
     * Handles the list of users from the communicator
     * Adds them to the UI
     * @param message the string of users separated by \n characters
     */
    public void handleUsers(String message) {
        logger.info("Adding the list of users in the lobby to the UI");
        var usersNames = message.split("\n");
        usersFlow.getChildren().clear();
        for (String user : usersNames) {
            var userText = new Text(user + " ");
            if (user.equals(nickname.get())) {
                userText.getStyleClass().add("myname");
            }
            usersFlow.getChildren().add(userText);
        }
    }
    
    public void stopUsersTimer() {
        usersTimer.cancel();
        usersTimer = null;
    }
}
