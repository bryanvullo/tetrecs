package uk.ac.soton.comp1206.component;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * This is a custom component which creates a UI display of every piece in the game
 */
public class PiecesDisplay extends GridPane {
    
    private static final Logger logger = LogManager.getLogger(PiecesDisplay.class);
    
    /**
     * Creates a custom component which creates a UI display of every piece in the game
     * @param window The GameWindow it will be placed in.
     *               The Width is dependent on GameWindow's width
     */
    public PiecesDisplay(GameWindow window) {
        logger.info("Creating the Pieces Display");
        var size = window.getWidth()/12;
        
        //display properties
        alignmentProperty().set(Pos.CENTER);
        setVgap(10);
        setHgap(10);
    
        int count = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                //creates a new piece board
                var pieceBoard = new PieceBoard(new Grid(3,3), size, size);
                pieceBoard.setPieceToDisplay(GamePiece.createPiece(count)); //creates a new piece
                add(pieceBoard, col, row); //add to the grid
                count++;
            }
        }
    }
}
