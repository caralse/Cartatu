package com.syndria.core;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundClip {

    private Clip clip;

    public SoundClip(String filePath) {
        try (InputStream audioSrc = this.getClass().getResourceAsStream("/gameResources/" + filePath);
             InputStream bufferedIn = new BufferedInputStream(audioSrc);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn))
        {
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.setMicrosecondPosition(0);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound in SoundClip: " + filePath);
            throw new RuntimeException(e);
        }
    }

    public void play() {
        play(false);
    }

    public void play(boolean loop) {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0); // Rewind// Stop if already running
            }
            clip.setMicrosecondPosition(0);
            clip.start();
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void close() {
        clip.stop();
        clip.flush();
        clip.close();
    }

    public boolean isActive() {
        return clip.isActive();
    }

    public boolean hasFinished() {
        return clip.getMicrosecondLength() == clip.getMicrosecondPosition();
    }

    public boolean isPlaying() {
        return clip.isRunning();
    }
}
