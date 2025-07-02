package game.states.match;

import com.syndria.state.State;
import com.syndria.time.Timer;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.UIComponent;
import game.entities.Card;

import java.util.ArrayList;

public class DealerTurn extends State {

    private Match match;
    private Timer t;

    public DealerTurn(String label, Match match) {
        super(label);
        this.match = match;
    }

    @Override
    public void enter() {
        t = new Timer(1);
    }

    @Override
    public void draw(double alpha) {

    }

    private void pickBestValues(FixedContainer hand) {
        for (UIComponent card : hand.getChildren()) {
            if (!((Card)card).getCardType().equals("ace")) {
                continue;
            }

            ArrayList<Integer> possibleValues = ((Card)card).getValuesList();
            int bestIndex = 0; // Default to the first value
            int bestScore = match.getDealerScoreValue();

            for (int i = 0; i < possibleValues.size(); i++) {
                int currentScore = match.getDealerScoreValue();
                // Temporarily pick the current value
                ((Card)card).pickValue(i);
                match.calculateScore();
                int newScore = match.getDealerScoreValue();
                // Update best score and index if current score is better and valid
                if (currentScore <= 21 && currentScore > bestScore) {
                    bestScore = currentScore;
                    bestIndex = i;
                } else if (currentScore > 21 && newScore < currentScore) {
                    bestScore = currentScore;
                    bestIndex = i;
                }
            }

            // If no valid scores exist, bestIndex remains the first value by default
            ((Card)card).pickValue(bestIndex);
            match.calculateScore();// Finalize the best value
        }
    }

    @Override
    public void update(double dt) {
        pickBestValues(match.dealerSide);
        if (match.getUserScoreValue() >= match.getDealerScoreValue() && match.getDealerScoreValue() < 21 && match.getUserScoreValue() <= 21 &&
            match.dealerSide.getChildren().size() < 5) {
            match.drawCard("dealer");
        } else {
            t.wait(dt);
            t.atEndTime(() -> {
                match.getMatchPhases().switchTo("damageCalc");
            });
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
