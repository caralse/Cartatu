package game.states.match;

import com.syndria.state.State;

public class InitialRound extends State {

    private Match match;
    private int i;

    public InitialRound(String label, Match match) {
        super(label);
        this.match = match;
    }

    @Override
    public void enter() {
        i = 0;
    }

    @Override
    public void draw(double alpha) {

    }

    @Override
    public void update(double dt) {
        if (i < 2) { //Draw two cards on first turn
            match.drawCard("dealer");
        } else if (i < 4) {
            match.drawCard("user");
        } else {
            match.getMatchPhases().switchTo("round");
        }
        i++;
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
