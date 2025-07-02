package game.states.match;

import com.syndria.math.Vector;
import com.syndria.state.State;
import game.entities.MatchButton;

public class Round extends State {

    private Match match;
    private MatchButton drawButton;
    private MatchButton standButton;
    private MatchButton quitButton;

    public Round(String label, Match match) {
        super(label);
        this.match = match;
        drawButton = new MatchButton("DRAW", new Vector(603, 307));
        standButton = new MatchButton("STAND", new Vector(603, 273));
        quitButton = new MatchButton("ESCI", new Vector(53, 402));

        drawButton.onClick(() -> {
            if (match.userSide.getChildren().size() < 5) {
                match.drawCard("user");
            }
        });

        standButton.onClick(() -> {
            match.getMatchPhases().switchTo("dealerTurn");
        });

        quitButton.onClick(() -> {
            match.userHealthBar.setValue(0);
            match.getMatchPhases().switchTo("damageCalc");
        });

    }

    @Override
    public void enter() {}

    @Override
    public void draw(double alpha) {
        drawButton.draw(alpha);
        standButton.draw(alpha);
        quitButton.draw(alpha);
    }

    @Override
    public void update(double dt) {
        drawButton.update(dt);
        standButton.update(dt);
        quitButton.update(dt);

        match.calculateScore();
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
