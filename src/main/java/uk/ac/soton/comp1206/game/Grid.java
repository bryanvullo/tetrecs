package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer
 * values arranged in a 2D arrow, with rows and columns.
 * <p>
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display
 * of the contents of the grid.
 * <p>
 * The Grid contains functions related to modifying the model, for example, placing a piece inside
 * the grid.
 * <p>
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {
    
    private static final Logger logger = LogManager.getLogger(Grid.class);
    
    /**
     * The number of columns in this grid
     */
    private final int cols;
    
    /**
     * The number of rows in this grid
     */
    private final int rows;
    
    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;
    
    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        
        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];
        
        //Add a SimpleIntegerProperty to every block in the grid
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }
    
    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used
     * for binding.
     *
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }
    
    /**
     * Update the value at the given x and y index within the grid
     *
     * @param x     column
     * @param y     row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }
    
    /**
     * Get the value represented at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }
    
    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }
    
    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * Returns true if the piece can be played at a certain coordinate
     *
     * @param piece the piece to play
     * @param x     the x coordinate of the clicked block
     * @param y     the y coordinate of the clicked block
     * @return if the piece can be played
     */
    public Boolean canPlayPiece(GamePiece piece, int x, int y) {
        logger.info("checking if {} can be played piece in coordinates ({},{})", piece, x, y);
        var blocks = piece.getBlocks();
        for (int pieceY = 0; pieceY < blocks.length; pieceY++) {
            for (int pieceX = 0; pieceX < blocks[pieceY].length; pieceX++) {
                if (blocks[pieceX][pieceY] > 0) {
                    int gridX = x + pieceX - 1; //calculating the grid coordinates
                    int gridY = y + pieceY - 1;
                    if (get(gridX, gridY) != 0) {
                        logger.info("unable to place piece");
                        return false;
                    }
                }
            }
        }
        logger.info("piece can be played");
        return true;
    }
    
    /**
     * plays the current piece onto the board
     *
     * @param piece the piece to play
     * @param x     the x coordinate of the clicked block
     * @param y     the y coordinate of the clicked block
     */
    public void playPiece(GamePiece piece, int x, int y) {
        logger.info("playing the piece {} in coordinates ({},{})", piece, x, y);
        var blocks = piece.getBlocks();
        for (int pieceY = 0; pieceY < blocks.length; pieceY++) {
            for (int pieceX = 0; pieceX < blocks[pieceY].length; pieceX++) {
                if (blocks[pieceX][pieceY] > 0) {
                    int gridX = x + pieceX - 1; //calculating the grid coordinates
                    int gridY = y + pieceY - 1;
                    set(gridX, gridY, blocks[pieceX][pieceY]);
                }
            }
        }
    }
}
