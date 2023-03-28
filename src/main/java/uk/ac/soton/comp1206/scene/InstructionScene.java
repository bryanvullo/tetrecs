package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * This scene displays the Instructions of how to play the game
 */
public class InstructionScene extends BaseScene {
    
    private static final Logger logger = LogManager.getLogger(InstructionScene.class);
    
    
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instruction Scene");
    }
    
    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building {}", this.getClass().getName());
        
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        
        var backPane = new StackPane();
        backPane.setMaxWidth(gameWindow.getWidth());
        backPane.setMaxHeight(gameWindow.getHeight());
        backPane.getStyleClass().add("menu-background");
        root.getChildren().add(backPane);
        
        var mainPane = new BorderPane();
        backPane.getChildren().add(mainPane);
        
        var top = new VBox();
        top.alignmentProperty().set(Pos.CENTER);
        top.setPadding(new Insets(10, 30, 20, 30));
        
        var title = new Text("Instructions");
        title.getStyleClass().add("title");
        
        var text = new Text("TetrECS is a fast-paced gravity-free block placement game. You must "
            + "survive by clearing lines through thoughtful placement of each block. Watch the timer!"
            + " Each time the timer runs out you lose a life");
        text.getStyleClass().add("heading");
        var flow = new TextFlow(text);
        
        top.getChildren().addAll(title, flow);
        mainPane.setTop(top);
        
        var instructionUri = getClass().getResource("/images/Instructions.png").toExternalForm();
        var instructionsImage = new ImageView(instructionUri);
        instructionsImage.setPreserveRatio(true);
        instructionsImage.setFitWidth(gameWindow.getWidth()/1.3);
        mainPane.setCenter(instructionsImage);
    }
    
    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
    
    }
}
