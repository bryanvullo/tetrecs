package uk.ac.soton.comp1206.event;

/**
 * This listener listens to when the player loses a life
 */
public interface LoseLifeListener {
    
    /**
     * The method that executes when a player loses a life
     * To be overridden on implementation
     */
    public void loseLife();
    
}
