package game.states.match.fx;

import com.syndria.math.Vector;
import com.syndria.time.Tween2D;
import game.entities.Card;
import game.states.match.Match;
import game.states.match.StateAnimation;

public class ApplyEffectAnimation extends StateAnimation {
    private Card card;
    private String side;
    private Tween2D tween;
    private Tween2D tween2;
    private Match match;

    public ApplyEffectAnimation(Card card, String side, Match match) {
        this.card = card;
        this.side = side;
        this.match = match;
    }

    @Override
    public void enter() {
        Vector basePosition = card.getPosition().copy();
        if (side.equals("user")) {
            Vector arrive = Vector.sub(basePosition, new Vector(0, 10));
            tween = new Tween2D(basePosition, arrive, 0.5);
            tween2 = new Tween2D(arrive, basePosition, 0.5);
        } else if (side.equals("dealer")) {
            Vector arrive = Vector.sum(basePosition, new Vector(0, 10));
            tween = new Tween2D(basePosition, arrive, 0.5);
            tween2 = new Tween2D(arrive, basePosition, 0.5);
        } else throw new RuntimeException();

        match.getMatchAudio().play("applyEffect");
    }

    @Override
    public void update(double dt) {
        card.setPosition(tween.update(dt));
        if (tween.isComplete()) {
            if (onComplete != null && !tween2.hasStarted()) {
                onComplete.run();
            }
            card.setPosition(tween2.update(dt));
            if (tween2.isComplete()) {
                match.getMatchPhases().pop();
            }
        }
    }
}
