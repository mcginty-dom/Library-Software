package Backend.Sounds;
/**
 * File Name: Sounds.java
 * Creation Date: 27/11/2018
 * Copyright: No Copyright
 *
 * @version 1.0
 * @author Ryan Lucas
 */
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public enum SoundEffects {

    errorMessage1("src/res/sounds/errorMessage1.wav"), // An error message sound for when an action in the system cannot be carried out
    errorMessage2("src/res/sounds/errorMessage2.wav"), // An error message sound for when an action in the system cannot be carried out
    notification1("src/res/sounds/notification1.wav"), // A sound to indicate a notification
    notification2("src/res/sounds/notification2.wav"), // A sound to indicate a notification
    moneyDeposit1("src/res/sounds/moneyDeposit1.wav"), // A sound to indicate money has been placed in the account
    pageChange1("src/res/sounds/pageChange1.wav"), // A sound to indicate page change
    newPage("src/res/sounds/buttonClick1.wav"), // A sound to indicate a button has been pressed
    createPageOpen("src/res/sounds/buttonClick2.wav"), // A sound to indicate a button has been pressed
    cancel("src/res/sounds/buttonClick3.wav"),
    submitButton("src/res/sounds/buttonClick4.wav"),
    resetButton("src/res/sounds/multimediaClick1.wav"),
    imageSelect("src/res/sounds/multimediaClick2.wav"),
    goodbye("src/res/sounds/goodbye.wav");


    private Clip clip; // Used to store the clip

    /**
     * Constructor to construct each element of the enum with its own sound file.
     * @param soundFileName The file name of the .wav file of the sound clip
     */
    SoundEffects(String soundFileName) {

        try {

            File soundFile = new File(soundFileName); // Opens the sound file as a file object

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile); // makes an audio input stream of the file

            clip = AudioSystem.getClip(); // Creates a clip of the input stream

            clip.open(audioInputStream); // Opens the audio stream

        } catch (UnsupportedAudioFileException e) { // Exception if the file selected is an unsupported audio file
            e.printStackTrace();
        } catch (LineUnavailableException b) { // Exception if line is unavailable
            b.printStackTrace();
        } catch (IOException c) { // Exception to cover getting and opening audioInputStream
            c.printStackTrace();
        }
    }

    /**
     * Plays the sound selected from the enum type
     */
    public void play() {
        clip.setFramePosition(0); // Rewind to the beginning
        clip.start(); // Start playing
        clip.drain(); // Empties buffer ready for next sound
    }

    /**
     * Used to test the functionality of the Sounds class.
     */
    public static void main(String[] args) {

        SoundEffects.errorMessage1.play();
        SoundEffects.notification1.play();
        SoundEffects.notification2.play();
        SoundEffects.moneyDeposit1.play();
        SoundEffects.pageChange1.play();
        SoundEffects.newPage.play();
        SoundEffects.createPageOpen.play();

    }
}

