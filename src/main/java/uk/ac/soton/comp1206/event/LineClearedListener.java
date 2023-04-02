package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * This Line Cleared Listener is used to listening when a line is cleared
 */
public interface LineClearedListener {
    
    /**
     * This is the method which will execute when a line is cleared
     * To be overridden on implementation
     * @param coordinates the coordinates of the blocks cleared
     */
    public void lineCleared(GameBlockCoordinate[] coordinates);
    
}
