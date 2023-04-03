package uk.ac.soton.comp1206.event;

/**
 * The Game Loop Listener is used to listen to when the game loop timer resets
 */
public interface GameLoopListener {
    
    /**
     * The method to execute on the event
     * To be overridden on implementation
     */
    public void gameLoop(int time);
    
}
