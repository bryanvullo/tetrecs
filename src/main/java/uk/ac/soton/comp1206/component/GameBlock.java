package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        Color translucentBlack = Color.color(1,1,1, 0.1);
        gc.setFill(translucentBlack);
        gc.fillRect(0,0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Color colour) {
        var gc = getGraphicsContext2D();
        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(colour.darker());
        gc.fillRect(0,0, width, height);
        
        //Inner Block
        gc.setFill(colour.invert().darker().invert());
        gc.fillRect(width/8, height/8, width/4*3, height/4*3);
        
        //Triangle Pattern
        gc.setFill(colour);
        gc.fillPolygon(new double[]{width/8, width/8, width/8*7},
            new double[]{height/8, height/8*7, height/8*7}, 3);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }
    
    /**
     * Method to highlight a block
     */
    public void highlightBlock() {
        var highlightColor = new Color(1, 1, 1, 0.3);
        var gc = getGraphicsContext2D();
    
        //Draw the highlight
        gc.setFill(highlightColor);
        gc.fillRect(0, 0, width, height);
    }
    
    /**
     * Method to paint a circle highlight in the middle of the block
     */
    public void paintCircle() {
        var circleColor = new Color(1, 1, 1, 0.7);
        var gc = getGraphicsContext2D();
        
        //Draw the circle
        gc.setFill(circleColor);
        gc.fillOval(width/4, height/4, width/2, height/2);
    }
    
    /**
     * Method to fade out a block
     * paints the block empty then paints it green and gradually reduces the opacity
     */
    public void fadeOut() {
        paintEmpty();
        //implements an Animation timer which paints the block green
        // slowly reduces the opacity of the green until it is transparent
        AnimationTimer timer = new AnimationTimer() {
            float opacity = 1;
            @Override
            public void handle(long now) {
                if (opacity <= 0.1) {
                    stop();
                    paintEmpty();
                } else {
                    var green = new Color(0, 1, 0, opacity);
                    paintColor(green);
                    opacity -= 0.025;
                }
            }
        };
        timer.start();
    }

}
