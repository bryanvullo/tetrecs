package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerScoresScene extends ScoresScene {
    
    private static final Logger logger = LogManager.getLogger(MultiplayerScoresScene.class);
    private MultiplayerGame game;
    private Leaderboard leaderboard;
    
    /**
     * Creates a Scene Object
     *
     * @param window the GameWindow to place the Scene in
     * @param game   The game object at the end state
     */
    public MultiplayerScoresScene(GameWindow window, Game game) {
        super(window, game);
        logger.info("Creating multiplayer scores scene");
        this.game = (MultiplayerGame) game;
    }
    
    /**
     * Initialise this scene. Called after creation Load the scores and update the scores list and
     * file if the game score beats any of the high scores
     */
    @Override
    public void initialise() {
        super.initialise();
        logger.info("Initialising Multiplayer scores scene");
        
        leaderboard.playersScoresProperty().bind(game.playersDataProperty());
        leaderboard.update();
        game.setScoresListener(() -> leaderboard.update());
        leaderboard.reveal();
    }
    
    /**
     * Build the layout of the scores scene
     */
    @Override
    public void build() {
        super.build();
        logger.info("Building multiplayer scores scene");
        scoreBox.getChildren().remove(scoreList);
    
        leaderboard = new Leaderboard("Versus");
        scoreBox.getChildren().add(0, leaderboard);
    }
    
    /**
     * Handles the case which the escape key has been pressed Returns to the Game Menu
     */
    @Override
    protected void handleEscape() {
        game.stopScoresTimer();
        super.handleEscape();
    }
}
