package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Mouse Hover Listener is used to listen to the event
 * in which the mouse is hovered over a GameBlock
 */
public interface MouseHoverListener {
    
    /**
     * The method which should execute when the listener catches an event
     * To be overridden upon implementation
     * @param block the block which is being hovered over
     */
    public void mouseHover(GameBlock block);
    
}
