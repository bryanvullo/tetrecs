package uk.ac.soton.comp1206.event;

/**
 * This Listener will listen to messages from the Game to display to the UI
 */
public interface MessageListener {
    
    /**
     * The method to execute when a message has been received
     * To be overridden on implementation
     * @param message the message received
     */
    public void receiveMessage(String message);
}
