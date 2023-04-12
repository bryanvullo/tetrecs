package uk.ac.soton.comp1206.event;

/**
 * This listener is used to listen to when the scores have changed in the multiplayer match
 */
public interface ChangedScoresListener {
    
    /**
     * The method to execute when the scores have changed
     * To be overridden on implementation
     */
    public void updateScores();
    
}
