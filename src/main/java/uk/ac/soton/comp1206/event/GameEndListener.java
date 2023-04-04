package uk.ac.soton.comp1206.event;

/**
 * This listener is used to listen to when the Game ends
 * Alert the UI that the game has ended
 */
public interface GameEndListener {
    
    /**
     * The method to execute for the action
     * To be overridden on implementation
     */
    public void gameEnd();
    
}
