package game.states;

import com.syndria.Syndria;
import com.syndria.core.SoundClip;
import com.syndria.gfx.Animation;
import com.syndria.state.State;
import com.syndria.state.StateManager;

public class Splash extends State {

    private StateManager sm;
    private Animation animation;
    private SoundClip splash;

    public Splash(String label, StateManager sm) {
        this.label = label;
        this.sm = sm;
        animation = new Animation("/gameResources/gfx/splash.png", 720);
        splash = new SoundClip("audio/music/splash.wav");
        animation.setFramesDurations(0.15);
    }

    @Override
    public void enter() {
        animation.setLoop(true);
        splash.play(true);
    }

    @Override
    public void draw(double alpha) {
        animation.draw();
    }

    @Override
    public void update(double dt) {
        animation.update(dt);
        if (Syndria.input.MousePressed(1)) {
            sm.switchTo("menu");
            splash.close();
            splash = null;
            animation.free();
            animation = null;
            System.gc();
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
