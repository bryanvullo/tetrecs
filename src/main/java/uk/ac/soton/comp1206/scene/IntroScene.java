package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * This Class creates a Scene which is used to display the ECS Logo animation
 */
public class IntroScene extends BaseScene {
    
    private static final Logger logger = LogManager.getLogger(IntroScene.class);
    private ImageView logo;
    
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     * This scene is used only for the Intro, the ECS Logo animation
     * @param gameWindow the game window
     */
    public IntroScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Intro Scene");
    }
    
    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
        logger.info("Initialising Intro Scene");
        fadeLogo();
    }
    
    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
    
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
    
        //gets the logo image
        var uri = getClass().getResource("/images/ECSGames.png").toExternalForm();
        logo = new ImageView(uri);
        logo.setFitWidth(gameWindow.getWidth());
        logo.setOpacity(0);
    
        //creates a new temporary pane which displays the logo
        var pane = new StackPane();
        pane.setMaxWidth(gameWindow.getWidth());
        pane.setMaxHeight(gameWindow.getHeight());
        pane.getStyleClass().add("intro");
    
        root.getChildren().add(pane);
        pane.getChildren().add(logo);
    }
    
    /**
     * Fade Animation of the ECS Logo
     * onFinished Property is set to display the menu
     */
    public void fadeLogo() {
        logger.info("Fading logo");
        var fade = new FadeTransition(Duration.millis(3000), logo);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
        fade.onFinishedProperty().set((event) -> { //once fade finishes, display menu
            gameWindow.startMenu();
        });
    }
}
