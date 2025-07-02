package com.syndria.core;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    // Store sound clips mapped to a string key
    private Map<String, Clip> soundClips;
    private Map<String, String> soundPaths;

    private Clip aux;

    // Load an audio file into the manager
    public void loadSound(String key, String filePath) {
        try (InputStream audioSrc = this.getClass().getResourceAsStream("/gameResources/" + filePath);
             InputStream bufferedIn = new BufferedInputStream(audioSrc);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn))
        {
            if(soundClips == null) {
                soundClips = new HashMap<>();
            }
            if (soundPaths == null) {
                soundPaths = new HashMap<>();
            }
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundClips.put(key, clip);
            soundPaths.put(key, "/gameResources/" + filePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound in AudioManager: " + filePath);
            throw new RuntimeException(e);
        }
    }

    // Play a sound
    public void play(String key) {
        play(key, false);
    }

    public void play(String key, boolean loop) {
        Clip clip = soundClips.get(key);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0);
            }
            clip.setMicrosecondPosition(0);
            clip.start();
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void quickPlay(String key) {
        String filePath = soundPaths.get(key);
        if (filePath == null) {
            System.err.println("Error: No sound path found for key: " + key);
            return;
        }
        try (InputStream audioSrc = this.getClass().getResourceAsStream(filePath);
             InputStream bufferedIn = new BufferedInputStream(audioSrc);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn))
        {
            aux = AudioSystem.getClip();
            aux.open(audioStream);
            aux.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound in quickPlay: " + filePath);
            e.printStackTrace();
        }
    }

    public void loop(String key) {
        Clip clip = soundClips.get(key);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Pause a sound
    public void pause(String key) {
        Clip clip = soundClips.get(key);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    // Stop a sound completely
    public void stop(String key) {
        Clip clip = soundClips.get(key);
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);  // Reset position
        }
    }

    // Stop any sound
    public void stop() {
        soundClips.forEach((name, clip) -> stop(name));
    }

    // Check if a sound is playing
    public boolean isPlaying(String key) {
        Clip clip = soundClips.get(key);
        return clip != null && clip.isRunning();
    }

    // Release resources when done
    public void closeAll() {
        for (Clip clip : soundClips.values()) {
            clip.flush();
            clip.close();
            clip = null;
        }
        soundClips.clear();
        soundClips = null;
        soundPaths.clear();
        soundPaths = null;
        if (aux != null) {
            aux.close();
            aux = null;
        }
    }
}
