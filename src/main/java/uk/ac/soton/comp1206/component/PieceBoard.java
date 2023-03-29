package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);
    
    private GamePiece piece;
    
    /**
     * Create a new GameBoard with its own internal grid, specifying the number of columns and rows,
     * along with the visual width and height.
     *
     * @param cols   number of columns for internal grid
     * @param rows   number of rows for internal grid
     * @param width  the visual width
     * @param height the visual height
     */
    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
        logger.info("Creating a Piece Board");
    }
    
    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     *
     * @param grid   linked grid
     * @param width  the visual width
     * @param height the visual height
     */
    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);
        logger.info("Creating a Piece Board");
    }
    
    /**
     * Sets a piece to display on the PieceBoard
     * @param piece The GamePiece to display
     */
    public void setPieceToDisplay(GamePiece piece) {
        if (piece == null) return;
        this.piece = piece;
        logger.info("Setting Piece {} in PieceBoard", piece);
        var pieceBlocks = piece.getBlocks();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                    grid.set(col, row, pieceBlocks[col][row]);
            }
        }
    }
    
    /**
     * Get the Piece that the board holds
     * @return the GamePiece
     */
    public GamePiece getPiece() {
        return piece;
    }
}
