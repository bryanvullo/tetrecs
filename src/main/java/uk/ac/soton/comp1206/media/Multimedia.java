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
    
        try {
            Media play = new Media(toPlay);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            audioEnabled = false;
            logger.error("unable to play audio, disabling audio");
        }
    }
    
    /**
     * Play a music file
     * @param musicFile The music file to play
     */
    public static void playMusic(String musicFile) {
        //checks if audio is enabled
        if (!audioEnabled) return;
        
        //stops previous music
        if (musicPlayer != null) musicPlayer.stop();
        
        //gets the external form of the music file
        String toPlay = getMediaFile(musicFile);
    
        try {
            Media play = new Media(toPlay);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.play();
        } catch (Exception e) {
            audioEnabled = false;
            logger.error("unable to play audio, disabling audio");
        }
        
        //auto loop the background music
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
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
     * @param playerName The name of the class variable which stores the media player you want to play on.
     * @param file The String representation of the Media file which you want to play on the player
     */
    private static void tryPlayMedia(String playerName, String file) {
        try {
            Media toPlay = new Media(file);
            var player = (MediaPlayer) Multimedia.class.getField(playerName).get(null);
            player = new MediaPlayer(toPlay);
            player.play();
        } catch (Exception e) {
            audioEnabled = false;
            logger.error("unable to play audio, disabling audio");
        }
    }
    
    public static void setOnMusicEnd(String otherMusicFile) {
        musicPlayer.setOnEndOfMedia(() -> playMusic(otherMusicFile));
    }
}
