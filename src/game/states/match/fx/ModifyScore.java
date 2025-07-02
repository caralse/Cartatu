package game.states.match.fx;

import com.syndria.math.Vector;
import com.syndria.time.Timer;
import game.entities.Score;
import game.states.match.Match;
import game.states.match.StateAnimation;

public class ModifyScore extends StateAnimation {

    private Timer timer;
    private Score score;
    private Match match;
    private String side;

    public ModifyScore (Match match, String type, String side) {
        this.match = match;
        this.side = side;
    }

    @Override
    public void enter() {
        timer = new Timer(0.5);
        match.getMatchAudio().play("buzz");
        Vector initialPos;

        if (side.equals("user")) {
            score = match.getUserScore();
        } else if (side.equals("dealer")) {
            score = match.getDealerScore();
        } else throw new RuntimeException();

        initialPos = score.getPosition().copy();

        timer.atEndTime(() -> {
            if (onComplete != null) {
                score.setPosition(initialPos);
                onComplete.run();
            }
            match.getMatchPhases().pop();
        });
    }

    @Override
    public void update(double dt) {
        timer.wait(dt);
        score.setPosition(Vector.sum(score.getPosition(), new Vector(0, 10 * Math.sin(timer.getElapsed() * 3600))));
    }
}
