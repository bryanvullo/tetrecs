package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom component to display the Scores
 */
public class ScoreList extends VBox {
    
    private static final Logger logger = LogManager.getLogger(ScoreList.class);
    private String title;
    public ListProperty<Pair<String, Integer>> pairs;
    private Text titleText;
    
    /**
     * Creates a score list with a title above all the scores
     * @param title the String to display above the Score List
     */
    public ScoreList(String title) {
        logger.info("Creating ScoreList");
        this.title = title;
        build();
    }
    
    /**
     * Builds the Score List, prepares for it to be updated
     */
    private void build() {
        titleText = new Text(title);
        titleText.getStyleClass().add("score-list");
        pairs = new SimpleListProperty<>();
        setAlignment(Pos.CENTER);
    }
    
    /**
     * Animates the Scores with a Fade In
     */
    public void reveal() {
        logger.info("Revealing the Scores");
        for (var node: getChildren()) {
            var animation = new FadeTransition(Duration.millis(2500), node);
            animation.setFromValue(0);
            animation.setToValue(1);
            animation.play();
        }
    }
    
    /**
     * Updates the List with the ListProperty that is bound
     */
    public void update() {
        logger.info("Updating The ScoreList");
        
        getChildren().clear();
        getChildren().add(titleText);
        
        int counter = 0; //used for styling
        for (var pair : pairs) {
            var box = new HBox();
            box.setAlignment(Pos.CENTER);
            getChildren().add(box);
            
            var userText = new Text(pair.getKey() + " : ");
            var scoreText = new Text(pair.getValue().toString());
            box.getChildren().addAll(userText, scoreText);
    
            //styling
            userText.getStyleClass().add("score-item");
            scoreText.getStyleClass().add("score-item");
            var extraEffect = "-fx-effect: dropshadow(gaussian, black, 1, 1.0, 1, 1); -fx-font-weight: 600;";
            switch (counter) {
                case 0 -> {
                    userText.setStyle("-fx-fill: gold; " + extraEffect);
                    scoreText.setStyle("-fx-fill: gold; " + extraEffect);
                }
                case 1 -> {
                    userText.setStyle("-fx-fill: silver; " + extraEffect);
                    scoreText.setStyle("-fx-fill: silver; " + extraEffect);
                }
                case 2 -> {
                    userText.setStyle("-fx-fill: #CD7F32; " + extraEffect);
                    scoreText.setStyle("-fx-fill: #CD7F32; " + extraEffect);
                }
                case 3 -> {
                    userText.setStyle("-fx-fill: deeppink");
                    scoreText.setStyle("-fx-fill: deeppink");
                }
                case 4 -> {
                    userText.setStyle("-fx-fill: red");
                    scoreText.setStyle("-fx-fill: red");
                }
                case 5 -> {
                    userText.setStyle("-fx-fill: orange");
                    scoreText.setStyle("-fx-fill: orange");
                }
                case 6 -> {
                    userText.setStyle("-fx-fill: yellow");
                    scoreText.setStyle("-fx-fill: yellow");
                }
                case 7 -> {
                    userText.setStyle("-fx-fill: lime");
                    scoreText.setStyle("-fx-fill: lime");
                }
                case 8 -> {
                    userText.setStyle("-fx-fill: darkturquoise");
                    scoreText.setStyle("-fx-fill: darkturquoise");
                }
                case 9 -> {
                    userText.setStyle("-fx-fill: deepskyblue");
                    scoreText.setStyle("-fx-fill: deepskyblue");
                }
            }
            counter++;
        }
    }
}
