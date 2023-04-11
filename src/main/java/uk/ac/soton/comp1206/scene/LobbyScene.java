package uk.ac.soton.comp1206.scene;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.LobbyChat;
import uk.ac.soton.comp1206.media.Multimedia;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * This scene is used to display the multiplayer lobbies to join or create your own
 */
public class LobbyScene extends BaseScene {
    
    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private Communicator communicator;
    private VBox currentGamesBox;
    private VBox leftBar;
    private Timer channelsTimer;
    private String currentGame;
    private VBox rightBar;
    private LobbyChat lobbyChat;
    private Boolean canCreateGame = true;
    
    /**
     * Create a new lobby scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating a Lobby Scene");
        communicator = gameWindow.getCommunicator();
    }
    
    /**
     * Initialise this scene. Called after creation
     * Sets up all the listeners on the scene and communicator
     */
    @Override
    public void initialise() {
        scene.setOnKeyPressed(keyEvent -> { //keyboard input to escape scene
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                logger.info("Escaping from Lobby Scene");
                communicator.send("PART");
                gameWindow.startMenu();
            }
        });
        scene.addPostLayoutPulseListener(this::jumpToBottom);
        
        communicator.addListener(this::receiveMessage);
        channelsTimer = new Timer("ChannelsThread");
        var task = new TimerTask() {
    
            /**
             * The action to be performed by this timer task.
             */
            @Override
            public void run() {
                requestChannels();
            }
        };
        channelsTimer.scheduleAtFixedRate(task, 500, 5000);
    }
    
    /**
     * Build the layout of the lobby scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
    
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
    
        var lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("challenge-background");
        root.getChildren().add(lobbyPane);
    
        var mainPane = new BorderPane();
        lobbyPane.getChildren().add(mainPane);
        
        //Title
        var title = new Text("Multiplayer");
        title.getStyleClass().add("title");
        var topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        mainPane.setTop(topBox);
        
        //Left Bar displays current games and hosting a new game option
        leftBar = new VBox();
        leftBar.setAlignment(Pos.CENTER);
        mainPane.setLeft(leftBar);
        
        var currentGamesText = new Text("Current Games");
        currentGamesText.getStyleClass().add("score-text");
        currentGamesBox = new VBox();
        currentGamesBox.setAlignment(Pos.CENTER);
        
        var hostGameText = new Text("Host New Game");
        hostGameText.getStyleClass().add("score-text");
        hostGameText.setOnMouseClicked(this::displayField);
        leftBar.getChildren().addAll(currentGamesText, currentGamesBox, hostGameText);
        
        //Displays the current lobby chat if in a lobby
        rightBar = new VBox();
        rightBar.setAlignment(Pos.CENTER);
        mainPane.setCenter(rightBar);
    }
    
    /**
     * Method to jump to bottom when scene changes
     * Calls the same method on the lobby chat if in a lobby
     */
    private void jumpToBottom() {
        if (lobbyChat == null) return;
        lobbyChat.jumpToBottom();
    }
    
    /**
     * Method to display the text field to create a game
     * @param event the mouse event
     */
    private void displayField(MouseEvent event) {
        if (!canCreateGame) return;
        canCreateGame = false;
        var field = new TextField();
        field.setId("createGameField");
        leftBar.getChildren().add(field);
        field.setPromptText("Enter Game Name");
        field.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                createGame(field.getText());
                leftBar.getChildren().remove(leftBar.lookup("#createGameField"));
            }
        });
    }
    
    /**
     * Handles when communicator receives JOIN protocol
     * @param name the name of the game
     */
    private void joinedGame(String name) {
        logger.info("Joined {}", name);
        currentGame = name;
        Multimedia.playAudio("sounds/pling.wav");
        
        var gameName = new Text(name);
        gameName.getStyleClass().add("score-text");
        lobbyChat = new LobbyChat(communicator, gameWindow.getHeight()/2);
        rightBar.getChildren().addAll(gameName, lobbyChat);
    }
    
    /**
     * Method to handle when the user leaves a game
     */
    private void partedGame() {
        logger.info("Left {}", currentGame);
        rightBar.getChildren().clear();
        canCreateGame = true;
    }
    
    /**
     * Method to send the CREATE protocol to communicator to create a game
     * @param name the name of the game
     */
    private void createGame(String name) {
        logger.info("Creating the game {}", name);
        String message = "CREATE " + name;
        communicator.send(message);
    }
    
    /**
     * Method to send the JOIN protocol to join a game through the communicator
     * @param name the name of the game
     */
    private void joinGame(String name) {
        logger.info("Joining the game {}", name);
        String message = "JOIN " + name;
        communicator.send(message);
    }
    
    /**
     * Method to request all the availible games from the communicator
     */
    private void requestChannels() {
        logger.info("Requesting all the channels available");
        communicator.send("LIST");
    }
    
    /**
     * Handles all incoming messages from the communicator and
     * calls the appropriate method to handle each one
     * @param message the message from the communicator
     */
    private void receiveMessage(String message) {
        var lines = message.split(" ", 2);
        var type = lines[0];
        if (lines.length == 1) {
            switch (type) {
                case "PARTED" -> Platform.runLater(this::partedGame);
                case "HOST" -> Platform.runLater(() -> lobbyChat.setHost(true));
//                case "START" -> //start game
            }
        } else {
            var content = lines[1];
            switch (type) {
                case "CHANNELS" -> Platform.runLater(() -> addGames(content));
                case "JOIN" -> Platform.runLater(() -> joinedGame(content));
                case "ERROR" -> Platform.runLater(() -> popUpError(content));
                case "MSG" -> Platform.runLater(() -> lobbyChat.receiveMessage(content));
                case "NICK" -> Platform.runLater(() -> lobbyChat.handleChangeName(content));
                case "USERS" -> Platform.runLater(() -> lobbyChat.handleUsers(content));
            }
        }
    }
    
    /**
     * method to display a pop-up window of an error
     * @param message The error message to display
     */
    private void popUpError(String message) {
        logger.error("Error from communicator: {}", message);
        var popUp = new Alert(AlertType.ERROR);
        popUp.setTitle("Error");
        popUp.setContentText(message);
        
        popUp.showAndWait();
    }
    
    /**
     * Adds all the games available to the UI
     * @param message a String with all the available games separated by '\n'
     */
    private void addGames(String message) {
        logger.info("Adding games {}", message);
        var games = message.split("\n");
        currentGamesBox.getChildren().clear();
        for (String game : games) {
            if (game.equals("")) continue;
            var gameText = new Text(game);
            gameText.getStyleClass().add("channelItem");
            if (game.equals(currentGame)) { //highlight the current game
                gameText.getStyleClass().add("selected");
            }
            currentGamesBox.getChildren().add(gameText);
            gameText.setOnMouseClicked(event -> joinGame(gameText.getText()));
        }
    }
}
