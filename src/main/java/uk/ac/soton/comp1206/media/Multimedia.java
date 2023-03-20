package uk.ac.soton.comp1206.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handles playing media such as audio
 */
public class Multimedia {
    
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static boolean audioEnabled = true;
    
    private static MediaPlayer audioPlayer;
    private static MediaPlayer musicPlayer;
    
    /**
     * Play an audio file
     * @param audioFile The audio file to play
     */
    public static void playAudio(String audioFile) {
        if (!audioEnabled) return; //check if audio has been disabled
        String toPlay = getMediaFile(audioFile); //get audio file
    
        tryPlayMedia(audioPlayer, toPlay);
    }
    
    /**
     * Play a music file
     * @param musicFile The music file to play
     */
    public static void playMusic(String musicFile) {
        if (!audioEnabled) return;
        String toPlay = getMediaFile(musicFile);
    
        tryPlayMedia(musicPlayer, toPlay);
    }
    
    /**
     * This method gets the external form (path) of the media file.
     * @param file The media file to fetch its path
     * @return The media file's path
     */
    private static String getMediaFile(String file) {
        String toPlay = Multimedia.class.getResource("/" + file).toExternalForm();
        logger.info("Playing audio: " + toPlay);
        return toPlay;
    }
    
    /**
     * This method tries to play a Media file.
     * If this method runs into an exception then audio is disabled for the application.
     * @param player The player which you want the Media file to play on
     * @param file The Media file which you want to play on the player
     */
    private static void tryPlayMedia(MediaPlayer player, String file) {
        try {
            Media play = new Media(file);
            player = new MediaPlayer(play); //TODO check if the reassignment is a problem for field variable
            player.play();
        } catch (Exception e) {
            audioEnabled = false;
            logger.error("unable to play audio, disabling audio");
        }
    }
}
