package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.media.Multimedia;
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
        Multimedia.playMusic("music/game.wav"); //background music
        
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
        topBar.alignmentProperty().set(Pos.CENTER);
        
        var challengeText = new Text("Challenge Mode"); //TODO to change dynamically later
        challengeText.getStyleClass().add("title");
        
        var scoreBox = new VBox();
        scoreBox.alignmentProperty().set(Pos.CENTER);
        var scoreText = new Text("Score");
        scoreText.getStyleClass().add("heading");
        var score = new Text();
        score.textProperty().bind(game.score.asString());
        score.getStyleClass().add("score");
        scoreBox.getChildren().addAll(scoreText, score);
        
        var livesBox = new VBox();
        livesBox.alignmentProperty().set(Pos.CENTER);
        var livesText = new Text("Lives");
        livesText.getStyleClass().add("heading");
        var lives = new Text();
        lives.textProperty().bind(game.lives.asString());
        lives.getStyleClass().add("lives");
        livesBox.getChildren().addAll(livesText, lives);
        
        topBar.getChildren().addAll(scoreBox, challengeText, livesBox);
        topBar.setSpacing(150);
        
        mainPane.setTop(topBar);
    
        //adding UI components: level, multiplier
        var sideBar = new VBox();
        sideBar.alignmentProperty().set(Pos.CENTER);
        
        var levelText = new Text("Level");
        levelText.getStyleClass().add("heading"); //adds styling from css file
        var level = new Text();
        level.textProperty().bind(game.level.asString()); //binds UI comp to game property
        level.getStyleClass().add("level");
    
        var multiText = new Text("Multiplier");
        multiText.getStyleClass().add("heading");
        var multiFlow = new TextFlow();
        multiFlow.textAlignmentProperty().set(TextAlignment.CENTER);
        var multi = new Text();
        multi.textProperty().bind(game.multiplier.asString());
        multi.getStyleClass().add("multiplier");
        var multiSymbol = new Text(" X");
        multiSymbol.getStyleClass().add("multiplier");
        multiFlow.getChildren().addAll(multi, multiSymbol);
        
        sideBar.getChildren().addAll(levelText, level, multiText, multiFlow);
        
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
