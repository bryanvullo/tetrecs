package uk.ac.soton.comp1206.scene;

import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);
        
        //adding UI components: score, lives
        var topBar = new HBox();
        var challengeText = new Text("Challenge Mode");
        challengeText.getStyleClass().add("title");
        
        var scoreBox = new VBox();
        var scoreText = new Text("Score");
        scoreText.getStyleClass().add("heading");
        var score = new Text();
        score.textProperty().bind(game.score.asString());
        score.getStyleClass().add("score");
        scoreBox.getChildren().addAll(scoreText, score);
        
        var livesBox = new VBox();
        var livesText = new Text("Lives");
        livesText.getStyleClass().add("heading");
        var lives = new Text();
        lives.textProperty().bind(game.lives.asString());
        lives.getStyleClass().add("lives");
        livesBox.getChildren().addAll(livesText, lives);
        
        topBar.getChildren().addAll(scoreBox, challengeText, livesBox);
        
        mainPane.setTop(topBar);
    
        //adding UI components: level, multiplier
        var sideBar = new VBox();
        
        var levelText = new Text("Level");
        levelText.getStyleClass().add("heading");
        var level = new Text();
        level.textProperty().bind(game.lives.asString());
        level.getStyleClass().add("level");
        
        sideBar.getChildren().addAll(levelText, level);
        
        mainPane.setRight(sideBar);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

}
