package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Next Piece Listener is used to listen to when a next piece is created
 */
public interface NextPieceListener {
    
    
    /**
     * The action of the listener on the next piece
     * This method will need to be overridden on implementation
     * @param current The current Game Piece
     * @param following the following Game Piece
     */
    public void nextPiece(GamePiece current, GamePiece following);
}
