package uk.ac.soton.comp1206.component;

import javafx.geometry.Pos;
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
public class PiecesDisplay extends VBox {
    
    private static final Logger logger = LogManager.getLogger(PiecesDisplay.class);
    
    /**
     * Creates a custom component which creates a UI display of every piece in the game
     */
    public PiecesDisplay(GameWindow window) {
        logger.info("Creating the Pieces Display");
        var size = window.getWidth()/12;
        setSpacing(10);
        
            //three rows of 5 game pieces
        //first row
        var row1 = new HBox();
        row1.setSpacing(10);
        row1.alignmentProperty().set(Pos.CENTER);
        
        var piece1 = new PieceBoard(new Grid(3,3), size, size);
        piece1.setPieceToDisplay(GamePiece.createPiece(0));
        var piece2 = new PieceBoard(new Grid(3,3), size, size);
        piece2.setPieceToDisplay(GamePiece.createPiece(1));
        var piece3 = new PieceBoard(new Grid(3,3), size, size);
        piece3.setPieceToDisplay(GamePiece.createPiece(2));
        var piece4 = new PieceBoard(new Grid(3,3), size, size);
        piece4.setPieceToDisplay(GamePiece.createPiece(3));
        var piece5 = new PieceBoard(new Grid(3,3), size, size);
        piece5.setPieceToDisplay(GamePiece.createPiece(4));
        row1.getChildren().addAll(piece1, piece2, piece3, piece4, piece5);
        
        //second row
        var row2 = new HBox();
        row2.setSpacing(10);
        row2.alignmentProperty().set(Pos.CENTER);
    
        var piece6 = new PieceBoard(new Grid(3,3), size, size);
        piece6.setPieceToDisplay(GamePiece.createPiece(5));
        var piece7 = new PieceBoard(new Grid(3,3), size, size);
        piece7.setPieceToDisplay(GamePiece.createPiece(6));
        var piece8 = new PieceBoard(new Grid(3,3), size, size);
        piece8.setPieceToDisplay(GamePiece.createPiece(7));
        var piece9 = new PieceBoard(new Grid(3,3), size, size);
        piece9.setPieceToDisplay(GamePiece.createPiece(8));
        var piece10 = new PieceBoard(new Grid(3,3), size, size);
        piece10.setPieceToDisplay(GamePiece.createPiece(9));
        row2.getChildren().addAll(piece6, piece7, piece8, piece9, piece10);
        
        //third row
        var row3 = new HBox();
        row3.setSpacing(10);
        row3.alignmentProperty().set(Pos.CENTER);
    
        var piece11 = new PieceBoard(new Grid(3,3), size, size);
        piece11.setPieceToDisplay(GamePiece.createPiece(10));
        var piece12 = new PieceBoard(new Grid(3,3), size, size);
        piece12.setPieceToDisplay(GamePiece.createPiece(11));
        var piece13 = new PieceBoard(new Grid(3,3), size, size);
        piece13.setPieceToDisplay(GamePiece.createPiece(12));
        var piece14 = new PieceBoard(new Grid(3,3), size, size);
        piece14.setPieceToDisplay(GamePiece.createPiece(13));
        var piece15 = new PieceBoard(new Grid(3,3), size, size);
        piece15.setPieceToDisplay(GamePiece.createPiece(14));
        row3.getChildren().addAll(piece11, piece12, piece13, piece14, piece15);
    
        getChildren().addAll(row1, row2, row3);
    }
}
