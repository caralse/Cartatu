package com.syndria.gfx;

public class Animation {

    private Image[] frames;
    private final int nFrames;
    private int frame = 0;
    private double[] framesDurations;
    private boolean finished = false;
    private boolean loop = false;
    private double t = 0d;

    public Animation (String path, int frameWidth) {
        Image spriteSheet = new Image(path);
        nFrames = spriteSheet.getW() / frameWidth;
        frames = new Image[nFrames];
        framesDurations = new double[nFrames];
        for (int i = 0; i < nFrames; i++) {
            frames[i] = Image.getQuad(spriteSheet, i * frameWidth, 0, frameWidth, spriteSheet.getH());
            framesDurations[i] = 0.05;
        }
        spriteSheet.free();
    }

    public void draw() {
        frames[frame].draw(0, 0);
    }

    public void update(double dt) {
        t += dt;
        if (t >= framesDurations[frame] && !finished) {
            t = 0;
            frame++;
        }

        if (frame == (nFrames - 1)) {
            if (loop) {
                frame = 0;
            } else {
                finished = true;
            }
        }

    }

    public boolean hasFinished() {
        return finished;
    }

    public int getFramesNumber() {
        return nFrames;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setFramesDurations(double[] durations) {
        framesDurations = durations;
    }

    public void setFramesDurations(double duration) {
        for (int i = 0; i < nFrames; i++) {
            framesDurations[i] = duration;
        }
    }

    public void free() {
        for (int i = 0; i < nFrames; i++) {
            frames[i].free();
        }
    }
}
