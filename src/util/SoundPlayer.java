package util;

import javax.sound.sampled.*;
import java.io.InputStream;

/**
 * SoundPlayer.java
 * Simple utility to play WAV sound files from the assets/ folder.
 * Uses Java's built-in javax.sound API — no external library needed.
 */
public class SoundPlayer {

    /**
     * Plays a WAV file from the classpath (assets/ folder).
     * Non-blocking: runs in a background thread so UI doesn't freeze.
     *
     * @param resourcePath e.g. "/assets/ufone_intro.wav"
     */
    public static void play(String resourcePath) {
        new Thread(() -> {
            try {
                InputStream is = SoundPlayer.class.getResourceAsStream(resourcePath);
                if (is == null) {
                    System.out.println("[SoundPlayer] File not found: " + resourcePath);
                    return;
                }
                AudioInputStream ais = AudioSystem.getAudioInputStream(
                        new java.io.BufferedInputStream(is));
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                // Wait for clip to finish, then close
                Thread.sleep(clip.getMicrosecondLength() / 1000 + 200);
                clip.close();
            } catch (Exception e) {
                // Sound is optional — silently skip if missing or unsupported
                System.out.println("[SoundPlayer] Could not play sound: " + e.getMessage());
            }
        }, "SoundThread").start();
    }

    /** Convenience: play startup intro sound */
    public static void playIntro() {
        play("/assets/ufone_intro.wav");
    }

    /** Convenience: play short click/success sound */
    public static void playClick() {
        play("/assets/click.wav");
    }
}
