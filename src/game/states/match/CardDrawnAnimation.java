package game.states.match;

import com.syndria.gfx.Image;
import com.syndria.time.Tween2D;
import com.syndria.math.Vector;

import java.util.Objects;

public class CardDrawnAnimation extends StateAnimation {

    public Tween2D tween;

    private Vector pos;
    private double duration;

    private Match match;

    private String path;
    private String side;

    private Image card;

    public CardDrawnAnimation(Match match, String side, double duration) {
        super();
        label = "CDA";
        this.side = side;
        path = "/gameResources/gfx/match/" + side + "Back.png";
        card = new Image(path);
        this.duration = duration;
        this.match= match;
    }

    @Override
    public void enter() {
        if (Objects.equals(side, "user")) {
            pos = new Vector(604, 363);
            Vector end = new Vector(323, 251);
            tween = new Tween2D(pos, end, duration);
        } else if (Objects.equals(side, "dealer")) {
            pos = new Vector(44, 11);
            Vector end = new Vector(323, 121);
            tween = new Tween2D(pos, end, duration);
        } else {
            System.out.println("Invalid side in CardDrawnAnimation.");
        }
        match.getMatchAudio().quickPlay("drawCard");
    }

    @Override
    public void draw(double alpha) {
        card.draw(pos);
    }

    @Override
    public void update(double dt) {
        pos = tween.update(dt);
        if (tween.isComplete()) {
            if (onComplete != null) {
                match.getMatchPhases().pop();
                onComplete.run();
            }
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }

}
