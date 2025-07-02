package com.syndria.core;

import com.syndria.gfx.Animation;
import com.syndria.state.State;
import com.syndria.state.StateManager;
import com.syndria.time.Timer;

public class SyndriaSplashState extends State {
    private Animation splash;
    private String exitState;
    private StateManager sm;
    private Timer timer;
    private Timer timer2;

    private final SoundClip harmonics = new SoundClip("audio/syndria.wav");

    public SyndriaSplashState(String label) {
        super(label);
    }

    public void setExitState(String exitState) {
        this.exitState = exitState;
    }

    public void setStateManager(StateManager sm) {
        this.sm = sm;
    }

    @Override
    public void enter() {
        harmonics.play();
        splash = new Animation("/assets/SyndriaSplashSheet.png", 720);
        splash.setFramesDurations(.07);
        timer = new Timer(.8);
        timer2 = new Timer(2);
    }

    @Override
    public void draw(double alpha) {
        splash.draw();
    }


    @Override
    public void update(double dt) {
        timer.wait(dt);
        if (timer.isComplete() && harmonics.isPlaying()) {
            splash.update(dt);
            if (splash.hasFinished()) {
                timer2.wait(dt);
                if (timer2.isComplete()) {
                    splash.free();
                    harmonics.close();
                    sm.switchTo(exitState);
                    sm.remove("SyndriaSplashState");
                }
            }
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
