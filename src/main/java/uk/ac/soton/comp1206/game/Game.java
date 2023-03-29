package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {
    
    private static final Logger logger = LogManager.getLogger(Game.class);
    
    /**
     * Bindable score integer property of the Game class
     */
    public IntegerProperty score = new SimpleIntegerProperty(0);
    /**
     * Bindable level integer property of the Game class
     */
    public IntegerProperty level = new SimpleIntegerProperty(0);
    /**
     * Bindable lives integer property of the Game class
     */
    public IntegerProperty lives = new SimpleIntegerProperty(3);
    /**
     * Bindable multiplier integer property of the Game class
     */
    public IntegerProperty multiplier = new SimpleIntegerProperty(1);
    
    /**
     * Number of rows
     */
    protected final int rows;
    
    /**
     * Number of columns
     */
    protected final int cols;
    
    /**
     * The grid model linked to the game
     */
    protected final Grid grid;
    
    /**
     * random field variable used to generate pieces
     */
    private final Random random = new Random();
    
    /**
     * field variable which stores the current piece model
     */
    public GamePiece currentPiece;
    public GamePiece followingPiece;
    
    public NextPieceListener nextPieceListener;
    
    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        
        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }
    
    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }
    
    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        followingPiece = spawnPiece();
        nextPiece();
    }
    
    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        
        logger.info("Block at ({},{}) has been clicked", x, y);
        
        if (!grid.canPlayPiece(currentPiece, x, y)) return; //checks piece can be placed
        
        grid.playPiece(currentPiece, x, y); //plays piece
        afterPiece(); //clear full rows and columns
        nextPiece(); //sets a new piece to the current piece
    }
    
    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }
    
    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }
    
    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * this method generates a random number and returns a new random GamePiece
     * @return random GamePiece
     */
    public GamePiece spawnPiece() {
        var piece = random.nextInt(GamePiece.PIECES);
        var gamePiece = GamePiece.createPiece(piece);
        logger.info("generating a new piece: {}", gamePiece);
        return gamePiece;
    }
    
    /**
     * replaces the current GamePiece with a new GamePiece
     */
    public void nextPiece() {
        currentPiece = followingPiece;
        logger.info("Current Piece is {}", currentPiece);
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }
    
    /**
     * logic to handle what happens after a GamePiece has been placed
     * Clearing lines
     */
    public void afterPiece() {
        logger.info("checking if any rows or columns are full");
        var blocksToClear = new HashSet<GameBlockCoordinate>();
        int linesToClear = 0;
        //checking rows
        for (int row = 0; row < rows; row++) {
            var counter = 0;
            for (int col = 0; col < cols; col++) {
                if (grid.get(col,row) > 0) counter++; //count how many occupied in this row
            }
            if (counter == cols) { //if row is full, add row to the clear list
                for (int col = 0; col < cols; col++) {
                    blocksToClear.add(new GameBlockCoordinate(col, row));
                }
                linesToClear++;
            }
        }
        //checking columns
        for (int col = 0; col < cols; col++) {
            var counter = 0;
            for (int row = 0; row < cols; row++) {
                if (grid.get(col,row) > 0) counter++; //count how many occupied in this row
            }
            if (counter == rows) { //if row is full, add row to the clear list
                for (int row = 0; row < rows; row++) {
                    blocksToClear.add(new GameBlockCoordinate(col, row));
                }
                linesToClear++;
            }
        }
        //clear all blocks in the clear list
        for (var block : blocksToClear) {
            grid.set(block.getX(), block.getY(), 0);
        }
    
        if (linesToClear > 0) {
            logger.info("clearing {} lines", linesToClear);
            score(linesToClear, blocksToClear.size()); //increase score
            multiplier.set(multiplier.get() + 1); //increase multiplier
        } else {
            multiplier.set(1);
        }
        
        //updating level
        level.set(score.get() / 1000);
    }
    
    /**
     * this method updates the score
     * @param lines number of lines cleared
     * @param blocks number of blocks cleared
     */
    private void score(int lines, int blocks) {
        score.set(score.get() + (lines * blocks * 10 * multiplier.get()));
    }
    
    /**
     * set a NextPieceListener use in the nextPiece method
     * @param listener NextPieceListener
     */
    public void setNextPieceListener(NextPieceListener listener) {
        nextPieceListener = listener;
    }
    
    /**
     * Method to rotate the current piece
     */
    public void rotateCurrentPiece() {
        currentPiece.rotate();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }
    
    /**
     * Method to rotate the current piece multiple times
     * @param rotations amount of times to rotate
     */
    public void rotateCurrentPiece(int rotations) {
        currentPiece.rotate(rotations);
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }
    
    /**
     * Method to swap the current and following pieces
     */
    public void swapCurrentPiece() {
        var temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }
}
