package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.MouseHoverListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    protected final int cols;

    /**
     * Number of rows in the board
     */
    protected final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    protected final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    protected final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    protected GameBlock[][] blocks;
    
    /**
     * The block the aim is currently set on
     */
    private GameBlock aimedBlock;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;
    
    /**
     * The listener to call when the board has been right-clicked
     */
    private RightClickedListener rightClickedListener;
    
    /**
     * The listener to call when the mouse is hovered on a block
     */
    private MouseHoverListener mouseHoverListener;

    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
            }
        }
        
        //Setting the aim at the top left
        aimedBlock = blocks[0][0];
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));
        
        //Events which get triggered when the mouse enters a game block
        block.setOnMouseEntered((e) -> mouseHoverListener.mouseHover(block));

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }
    
    /**
     * Set the listener to handle an event when the game board is right-clicked
     * @param listener the listener to set on the event
     */
    public void setOnRightClicked(RightClickedListener listener) {
        this.rightClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);
        if (event.getButton() == MouseButton.PRIMARY) {
            if (blockClickedListener != null) {
                blockClickedListener.blockClicked(block);
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            rightClickedListener.rightClick();
        }
    }
    
    /**
     * Handle event to highlight the block where the aim is at
     * @param block the block to highlight
     */
    public void aimEnteredBlock(GameBlock block) {
        aimExitedBlock(aimedBlock);
        aimedBlock = block;
        block.highlightBlock();
    }
    
    /**
     * Handle event to remove highlighting on the block the aim just exited
     * @param block the block to remove highlighting
     */
    private void aimExitedBlock(GameBlock block) {
        block.paint();
    }
    
    /**
     * This method is used to move the 'Aimed' block by a certain offset defined by the parameters
     * @param x how much the aim is moving horizontally
     * @param y how much the aim is moving vertically
     */
    public void moveAimedBlock(int x, int y) {
        var block = blocks[aimedBlock.getX() + x][aimedBlock.getY() + y];
        aimEnteredBlock(block);
    }
    
    /**
     * This method sets the listener for the Mouse Hover Event
     * @param listener the listener to be set
     */
    public void setOnHover(MouseHoverListener listener) {
        mouseHoverListener = listener;
    }

}
