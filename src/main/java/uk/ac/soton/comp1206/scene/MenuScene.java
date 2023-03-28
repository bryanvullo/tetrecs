package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.media.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private ImageView logo;
    private StackPane menuPane;
    
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
    
        menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var button = new Button("Play");
        mainPane.setCenter(button);

        //Bind the button action to the startGame method in the menu
        button.setOnAction(this::startGame);
    }

    /**
     * Initialise the menu
     * Displays animated logo, then opens the menu screen
     */
    @Override
    public void initialise() {
        logger.info("initialising the menu scene");
        Multimedia.playMusic("music/menu.mp3"); //background music
        
        //gets the logo image
        var uri = getClass().getResource("/images/ECSGames.png").toExternalForm();
        logo = new ImageView(uri);
        logo.setFitWidth(gameWindow.getWidth());
        logo.setOpacity(0);
        
        //creates a new temporary pane which displays the logo
        var pane = new StackPane();
        pane.setMaxWidth(gameWindow.getWidth());
        pane.setMaxHeight(gameWindow.getHeight());
        pane.getStyleClass().add("menu-black-fill");
        
        root.getChildren().add(pane);
        pane.getChildren().add(logo);
        
        //fade the logo
        fadeLogo();
    }
    
    /**
     * Fade Animation of the ECS Logo
     * onFinished Property is set to display the menu
     */
    public void fadeLogo() {
        logger.info("fading logo");
        var fade = new FadeTransition(Duration.millis(3000), logo);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
        fade.onFinishedProperty().set((event) -> { //once fade finishes, display menu
            root.getChildren().clear();
            root.getChildren().add(menuPane);
        });
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

}
