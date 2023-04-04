package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        titleText.getStyleClass().add("scorelist");
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
        
        for (var pair : pairs) {
            var box = new HBox();
            box.setAlignment(Pos.CENTER);
            getChildren().add(box);
            
            var userText = new Text(pair.getKey() + " : ");
            userText.getStyleClass().add("scoreitem");
            
            var scoreText = new Text(pair.getValue().toString());
            scoreText.getStyleClass().add("scoreitem");
            
            box.getChildren().addAll(userText, scoreText);
        }
    }
}
