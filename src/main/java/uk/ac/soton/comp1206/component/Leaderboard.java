package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Triplet;

/**
 * This custom component is used to display the online scores of all the players in a lobby
 */
public class Leaderboard extends ScoreList {
    
    private static final Logger logger = LogManager.getLogger(Leaderboard.class);
    private SimpleListProperty<Triplet<String, String, Integer>> playersScores;
    
    /**
     * Creates a leaderboard with a title above all the scores
     *
     * @param title the String to display above the LeaderBoard
     */
    public Leaderboard(String title) {
        super(title);
        playersScores = new SimpleListProperty<>();
    }
    
    /**
     * Overridden update method for different styling of the leaderboard
     */
    @Override
    public void update() {
        logger.info("Updating The Leaderboard");
    
        getChildren().clear();
        getChildren().add(titleText);
        
        for (var triplet : playersScores) {
            var box = new HBox();
            box.setAlignment(Pos.CENTER);
            getChildren().add(box);
    
            var name = triplet.get0();
            var lives = triplet.get1();
            var score = triplet.get2().toString();
    
            var playerText = new Text(name + " : " + score);
            box.getChildren().add(playerText);
            
            //styling
            playerText.getStyleClass().add("score-item");
            //use lives to style the player : score Text
            switch (lives) {
                case "DEAD" -> {
                    playerText.getStyleClass().add("deadscore");
                    playerText.setStyle("-fx-fill: red;");
                }
                case "1" -> {
                    playerText.setStyle("-fx-fill: red;");
                }
                case "2" -> {
                    playerText.setStyle("-fx-fill: yellow;");
                }
                case "3" -> {
                    playerText.setStyle("-fx-fill: lime;");
                }
            }
        }
    }
    
    /**
     * Exposes the playerScores list property to be able to bind it
     * @return the playerScores list property
     */
    public SimpleListProperty<Triplet<String, String, Integer>> playersScoresProperty() {
        return playersScores;
    }
}
