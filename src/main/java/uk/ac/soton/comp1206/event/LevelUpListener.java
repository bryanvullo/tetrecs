package uk.ac.soton.comp1206.event;

/**
 * This listener listens to when the game increases in level
 */
public interface LevelUpListener {
    
    /**
     * Method to call when game increases in level
     * To be overridden on implementation
     */
    public void levelUp();
    
}
