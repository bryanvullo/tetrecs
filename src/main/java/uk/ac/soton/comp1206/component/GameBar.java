package uk.ac.soton.comp1206.component;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

/**
 * This class is a custom component which creates the top bar of the game windows
 */
public class GameBar extends VBox {
    private static final Logger logger = LogManager.getLogger(GameBar.class);
    
    String title;
    private Text score;
    private Text multiplier;
    private Text level;
    private Text lives;
    
    /**
     * This is the constructor for the GameBar which takes a title
     * @param title the title at the top of the GameBar
     */
    public GameBar(String title) {
        logger.info("Building the Game Bar");
        this.title = title; //title is changed: single/multi-player
        build();
    }
    
    private void build() {
        logger.info("Building The Game Bar");
        alignmentProperty().set(Pos.CENTER);
        
        //title of GameBar
        var titleText = new Text(title);
        titleText.getStyleClass().add("title");
        getChildren().add(titleText);
    
        //score box UI components
        var scoreBox = new VBox();
        scoreBox.alignmentProperty().set(Pos.CENTER);
        var scoreText = new Text("Score");
        scoreText.getStyleClass().add("heading");
        score = new Text();
        score.getStyleClass().add("score");
        scoreBox.getChildren().addAll(scoreText, score);
    
        //lives box UI components
        var livesBox = new VBox();
        livesBox.alignmentProperty().set(Pos.CENTER);
        var livesText = new Text("Lives");
        livesText.getStyleClass().add("heading");
        lives = new Text();
        lives.getStyleClass().add("lives");
        livesBox.getChildren().addAll(livesText, lives);
    
        //level box UI components
        var levelBox = new VBox();
        levelBox.alignmentProperty().set(Pos.CENTER);
        var levelText = new Text("Level");
        levelText.getStyleClass().add("heading");
        level = new Text();
        level.getStyleClass().add("level");
        levelBox.getChildren().addAll(levelText, level);
    
        //multiplier box UI components
        var multiBox = new VBox();
        multiBox.alignmentProperty().set(Pos.CENTER);
        var multiText = new Text("Multiplier");
        multiText.getStyleClass().add("heading");
        var multiFlow = new TextFlow();
        multiFlow.textAlignmentProperty().set(TextAlignment.CENTER);
        multiplier = new Text();
        multiplier.getStyleClass().add("multiplier");
        var multiSymbol = new Text(" X");
        multiSymbol.getStyleClass().add("multiplier");
        multiFlow.getChildren().addAll(multiplier, multiSymbol);
        multiBox.getChildren().addAll(multiText, multiFlow);
    
        //current game info bar
        var gameDataBar = new HBox();
        gameDataBar.getChildren().addAll(levelBox, scoreBox, multiBox, livesBox);
        gameDataBar.alignmentProperty().set(Pos.CENTER);
        gameDataBar.setSpacing(100);
        
        getChildren().add(gameDataBar);
    }
    
    /**
     * This method exposes the score StringProperty
     * @return the score StringProperty
     */
    public StringProperty scoreProperty() {
        logger.info("exposing the Score Property");
        return score.textProperty();
    }
    
    /**
     * This method exposes the multiplier StringProperty
     * @return the multiplier StringProperty
     */
    public StringProperty multiplierProperty() {
        logger.info("exposing the Multiplier Property");
        return multiplier.textProperty();
    }
    
    /**
     * This method exposes the level StringProperty
     * @return the level StringProperty
     */
    public StringProperty levelProperty() {
        logger.info("exposing the Level Property");
        return level.textProperty();
    }
    
    /**
     * This method exposes the lives StringProperty
     * @return the lives StringProperty
     */
    public StringProperty livesProperty() {
        logger.info("exposing the Lives Property");
        return lives.textProperty();
    }
}
