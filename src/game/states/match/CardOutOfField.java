package game.states.match;

import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.time.Tween2D;
import game.entities.Card;

public class CardOutOfField extends State {

    private Card card;
    private Tween2D tween;
    private Runnable onComplete;
    private Match match;

    public CardOutOfField(Card card, Match match) {
        super("COOF");
        this.card = card;
        this.match = match;
    }

    @Override
    public void enter() {
        tween = new Tween2D(card.getPosition().copy(), new Vector(44, 121), 0.3);
        match.getMatchAudio().quickPlay("pop");
    }

    @Override
    public void draw(double alpha) {
    }

    @Override
    public void update(double dt) {
        card.setPosition(tween.update(dt));
        if (tween.isComplete()) {
            if (onComplete != null) {
                match.getMatchPhases().pop();
                onComplete.run();
            }
        }
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
