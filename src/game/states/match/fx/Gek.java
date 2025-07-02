package game.states.match.fx;

import com.syndria.Syndria;
import com.syndria.math.Vector;
import com.syndria.time.Timer;
import com.syndria.time.Tween;
import com.syndria.time.Tween2D;
import game.states.match.Match;
import game.states.match.StateAnimation;

public class Gek extends StateAnimation {

    Tween2D tween;
    Tween2D tween2;

    Vector pos;
    Vector dim;

    Timer pause;
    Tween fade;

    private int color;

    private Match match;

    public Gek(Match match) {
        this.match = match;
    }

    @Override
    public void enter() {
        pos = Syndria.gfx.getScreenCenter();
        dim = new Vector(0, 0);
        tween = new Tween2D(pos, new Vector(0, 0), 0.7);
        tween2 = new Tween2D(dim, new Vector(720, 480), 0.7);
        match.getMatchAudio().play("bomb");
        pause = new Timer(0.3);
        fade = new Tween(0xFF000000, 0x00000000, 0.7);
        color = 0xFF000000;
    }

    @Override
    public void update(double dt) {
        pos = tween.update(dt);
        dim = tween2.update(dt);
        if (tween.isComplete()) {
            if (onComplete != null && !pause.hasStarted()) {
                onComplete.run();
            }
            pause.wait(dt);
            if (pause.isComplete()) {
                color = (int)fade.update(dt);
                if (fade.isComplete()) {
                    match.getMatchPhases().pop();
                }
            }
        }
    }

    @Override
    public void draw(double alpha) {
        Syndria.gfx.drawRect(pos, dim, color);
    }
}
