package game.states.match;

import com.syndria.state.State;
import com.syndria.state.StateManager;

public abstract class StateAnimation extends State {
    protected Runnable onComplete;
    protected StateManager sm;

    public StateAnimation() {
        super();
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    public void enter() {

    }

    @Override
    public void draw(double alpha) {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
