package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
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

        //Beautiful title
        var titleUri = getClass().getResource("/images/TetrECS.png").toExternalForm();
        var title = new ImageView(titleUri);
        title.setFitWidth(gameWindow.getWidth()/1.3);
        title.setPreserveRatio(true);
        mainPane.setCenter(title);
    
            //Menu Options
        var menuOptions = new VBox();
        mainPane.setBottom(menuOptions);
        menuOptions.alignmentProperty().set(Pos.TOP_CENTER);
        
        //single-player
        var singlePlayer = new Text("Single Player");
        singlePlayer.getStyleClass().add("menuItem");
        menuOptions.getChildren().add(singlePlayer);
        //Bind the text 'Single Player' clicked action to startGame method in the menu
        singlePlayer.setOnMouseClicked(this::startGame);
        
        //multi-player
        var multiPlayer = new Text("Multi Player");
        multiPlayer.getStyleClass().add("menuItem");
        menuOptions.getChildren().add(multiPlayer);
        //Bind the text 'Multi Player' clicked action to startGame method in the menu TODO change this
//        multiPlayer.setOnMouseClicked(this::startGame);
        
        //instructions screen
        var instructions = new Text("How To Play");
        instructions.getStyleClass().add("menuItem");
        menuOptions.getChildren().add(instructions);
        //Bind the text 'How To Play' clicked action to openInstructions method in the menu
        instructions.setOnMouseClicked(this::openInstructions);
        
        //exit game
        var exit = new Text("Exit");
        exit.getStyleClass().add("menuItem");
        menuOptions.getChildren().add(exit);
        //Bind the text 'Exit' clicked action to quit method in the menu
        exit.setOnMouseClicked(this::quit);
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
        pane.getStyleClass().add("intro");
        
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
     * Handle when the Single Player Text is pressed
     * @param event event
     */
    private void startGame(MouseEvent event) {
        gameWindow.startChallenge();
    }
    
    /**
     * Handle when the 'How To Play' Text is pressed
     * @param event event
     */
    private void openInstructions(MouseEvent event) {
        gameWindow.startInstructions();
    }
    
    /**
     * Handle when the user presses 'Exit'
     * Closes the Application
     * @param event event
     */
    private void quit(MouseEvent event) {
        logger.info("Closing the Application");
        App.getInstance().shutdown();
    }

}
