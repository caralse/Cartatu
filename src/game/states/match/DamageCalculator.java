package game.states.match;

import com.syndria.state.State;
import com.syndria.ui.Alignment;
import com.syndria.ui.UIComponent;
import game.entities.Card;
import game.states.match.fx.ApplyEffectAnimation;

import java.util.ArrayList;
import java.util.List;

public class DamageCalculator extends State {

    private final Match match;
    private int phase;
    protected ArrayList<Card> userEffects;
    protected ArrayList<Card> dealerEffects;
    private static int SCALE = 2;

    public DamageCalculator(String label, Match match) {
        super(label);
        this.match = match;
    }

    @Override
    public void enter() {
        phase = 0;
        loadEffects();
        match.setDealerMult(1d);
        match.setUserMult(1d);
    }

    @Override
    public void update(double dt) {
        if (!applyEffects()) {
            if (phase < 5) {
                damageAnimation(phase);
                phase++;
            } else {
                clearField();
            }
        }
    }

    @Override
    public void draw(double alpha) {}

    public static int calcBaseDamage(int score) {
        return (score > 21) ? score - 21 : 0;
    }

    private static int calcBoost(int score) {
        return (score == 21) ? 10 : 0;
    }

    private void loadEffects() {
        userEffects = new ArrayList<>();
        dealerEffects = new ArrayList<>();

        for (UIComponent card : match.userSideBoost.getChildren()) {
            if (((Card)card).getActiveEffect()) {
                userEffects.add((Card)card);
            }
        }
        for (UIComponent card : match.dealerSideBoost.getChildren()) {
            if (((Card)card).getActiveEffect()) {
                dealerEffects.add((Card) card);
            }
        }

        userEffects.sort((a, b) -> {
            if(a.priority == b.priority)
                return 0;
            return a.priority < b.priority ? -1 : 1;
        });
        dealerEffects.sort((a, b) -> {
            if(a.priority == b.priority)
                return 0;
            return a.priority < b.priority ? -1 : 1;
        });
    }

    private boolean applyEffects() {
        Card card;
        ApplyEffectAnimation AEA;
        if (!userEffects.isEmpty()) {
            card = userEffects.removeFirst();
            AEA = new ApplyEffectAnimation(card, "user", match);
            match.getMatchPhases().push(AEA);
            AEA.setOnComplete(() -> {
                Card.getEffect(card.getCardName()).applyEffect(this, "user");
            });
            checkGameEnd();
            return true;
        } else if (!dealerEffects.isEmpty()) {
            card = dealerEffects.removeFirst();
            AEA = new ApplyEffectAnimation(card, "dealer", match);
            match.getMatchPhases().push(AEA);
            AEA.setOnComplete(() -> {
                Card.getEffect(card.getCardName()).applyEffect(this, "dealer");
            });
            checkGameEnd();
            return true;
        } else {
            return false;
        }
    }

    private void clearField() {
        for (UIComponent card : match.dealerSideBoost.getChildren()) {
            if (((Card)card).isOneShot()) {
                CardOutOfField coof = new CardOutOfField((Card)card, match);
                match.getMatchPhases().push(coof);
                coof.setOnComplete(() -> {
                    match.dealerSideBoost.getChildren().remove(card);
                    match.discardedContainer.add(card, Alignment.centerAbsolute(), false);
                    match.dealerDeck.discard(((Card) card).getCardName());
                });
                return;
            }
        }
        for (UIComponent card : match.userSideBoost.getChildren()) {
            if (((Card)card).isOneShot()) {
                CardOutOfField coof = new CardOutOfField((Card)card, match);
                match.getMatchPhases().push(coof);
                coof.setOnComplete(() -> {
                    match.userSideBoost.getChildren().remove(card);
                    match.discardedContainer.add(card, Alignment.centerAbsolute(), false);
                    match.userDeck.discard(((Card) card).getCardName());
                });
                return;
            }
        }

        if (!match.dealerSide.getChildren().isEmpty()) {
            CardOutOfField coof = new CardOutOfField((Card) match.dealerSide.getChildren().getFirst(), match);
            match.dealerDeck.discard(((Card) match.dealerSide.getChildren().getFirst()).getCardName());
            match.getMatchPhases().push(coof);
            coof.setOnComplete(() -> match.discardedContainer.add(match.dealerSide.getChildren().removeFirst(), Alignment.centerAbsolute(), false));
        } else if (!match.userSide.getChildren().isEmpty()) {
            CardOutOfField coof = new CardOutOfField((Card) match.userSide.getChildren().getFirst(), match);
            match.userDeck.discard(((Card) match.userSide.getChildren().getFirst()).getCardName());
            match.getMatchPhases().push(coof);
            coof.setOnComplete(() -> match.discardedContainer.add(match.userSide.getChildren().removeFirst(), Alignment.centerAbsolute(), false));
        } else {
            if (!match.discardedContainer.getChildren().isEmpty()) {
                Card card = (Card) match.discardedContainer.getChildren().getLast();
                match.discardedContainer.clear();
                match.discardedContainer.add(card, Alignment.centerAbsolute(), false);
            }
            match.dealerSide.clear();
            match.userSide.clear();
            if (!checkGameEnd()) {
                match.getMatchPhases().clearStack();
                match.getMatchPhases().switchTo("initial");
            }
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }

    private boolean checkGameEnd() {
        if (match.userHealthBar.getValue() == 0 || match.dealerHealthBar.getValue() == 0) {
            match.getMatchPhases().switchTo("endMatch");
            return true;
        }
        return false;
    }

    private void damageAnimation(int phase) {
        int user = match.getUserScoreValue();
        int dealer = match.getDealerScoreValue();
        switch (phase) {
            case 0: pushAnimation((int)(match.userMult * calcBaseDamage(user)), "user", "base");
                    break;
            case 1: pushAnimation(calcBoost(dealer), "user", "21");
                    break;
            case 2: pushAnimation((int)(match.dealerMult * calcBaseDamage(dealer)), "dealer", "base");
                    break;
            case 3: pushAnimation(calcBoost(user), "dealer", "21");
                    break;
            case 4: {
                    int delta = Math.abs(dealer - user);
                    if (user > dealer && user <= 21) {
                        pushAnimation(delta, "dealer", "base");
                    } else if (user < dealer && dealer <= 21) {
                        pushAnimation(delta, "user", "base");
                    } else if (user > dealer) {
                        pushAnimation((int)(match.userMult * delta), "user", "base");
                    } else {
                        pushAnimation((int)(match.dealerMult * delta), "dealer", "base");
                    }
                    break;
                }
            default: return;
        }
    }

    private void pushAnimation(int dmg, String side, String type) {
        dmg *= -SCALE;
        if (dmg < 0) {
            DamageAnimation dmgAn = new DamageAnimation(side, dmg, type, match);
            match.getMatchPhases().push(dmgAn);
            int finalDmg = dmg;
            dmgAn.setOnComplete(() -> {
                if (side.equals("user")) {
                    match.userHealthBar.addValue(finalDmg);
                } else {
                    match.dealerHealthBar.addValue(finalDmg);
                }
            });
        }
    }

    public Match getMatch() {
        return match;
    }

    public List getDealerEffects() {
        return dealerEffects;
    }

    public List getUserEffects() {
        return userEffects;
    }
}

