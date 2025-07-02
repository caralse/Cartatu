package game.states.online;

import com.syndria.ui.Alignment;
import com.syndria.ui.UIComponent;
import game.entities.Card;
import game.states.match.CardOutOfField;
import game.states.match.DamageAnimation;
import game.states.match.DamageCalculator;
import game.states.match.fx.ApplyEffectAnimation;

import java.util.ArrayList;

public class OnlineDamageCalculator extends DamageCalculator {

    private final OnlineMatch match;
    private int phase;
    private final ArrayList<Card> activeEffects = new ArrayList<>();
    private static int SCALE = 2;

    public OnlineDamageCalculator(String label, OnlineMatch match) {
        super(label, match);
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

    public static int calcBaseDamage(int score) {
        return (score > 21) ? score - 21 : 0;
    }

    private static int calcBoost(int score) {
        return (score == 21) ? 10 : 0;
    }

    private void loadEffects() {
        userEffects = new ArrayList<>();
        dealerEffects = new ArrayList<>();

        activeEffects.clear();

        for (UIComponent card : match.getUserSideBoost().getChildren()) {
            if (((Card)card).getActiveEffect()) {
                ((Card) card).setSide("user");
                activeEffects.add((Card) card);
                userEffects.add((Card) card);
            }
        }
        for (UIComponent card : match.getDealerSideBoost().getChildren()) {
            if (((Card)card).getActiveEffect()) {
                ((Card) card).setSide("dealer");
                activeEffects.add((Card) card);
                dealerEffects.add((Card) card);
            }
        }

        activeEffects.sort((a, b) -> {
            if(a.priority == b.priority)
                return 0;
            return a.priority < b.priority ? -1 : 1;
        });
    }

    private boolean applyEffects() {
        Card card;
        ApplyEffectAnimation AEA;
        if (!activeEffects.isEmpty()) {
            card = activeEffects.removeFirst();
            AEA = new ApplyEffectAnimation(card, card.getSide(), match);
            match.getMatchPhases().push(AEA);
            AEA.setOnComplete(() -> {
                Card.getEffect(card.getCardName()).applyEffect(this, card.getSide());
            });
            checkGameEnd();
            return true;
        } else {
            return false;
        }
    }

    private void clearField() {
        for (UIComponent card : match.getDealerSideBoost().getChildren()) {
            if (((Card)card).isOneShot()) {
                CardOutOfField coof = new CardOutOfField((Card)card, match);
                match.getMatchPhases().push(coof);
                coof.setOnComplete(() -> {
                    match.getDealerSideBoost().getChildren().remove(card);
                    match.getDiscardedContainer().add(card, Alignment.centerAbsolute(), false);
                    match.getDealerDeck().discard(((Card) card).getCardName());
                });
                return;
            }
        }
        for (UIComponent card : match.getUserSideBoost().getChildren()) {
            if (((Card)card).isOneShot()) {
                CardOutOfField coof = new CardOutOfField((Card)card, match);
                match.getMatchPhases().push(coof);
                coof.setOnComplete(() -> {
                    match.getUserSideBoost().getChildren().remove(card);
                    match.getDiscardedContainer().add(card, Alignment.centerAbsolute(), false);
                    match.getUserDeck().discard(((Card) card).getCardName());
                });
                return;
            }
        }

        if (!match.getDealerSide().getChildren().isEmpty()) {
            CardOutOfField coof = new CardOutOfField((Card) match.getDealerSide().getChildren().getFirst(), match);
            match.getDealerDeck().discard(((Card) match.getDealerSide().getChildren().getFirst()).getCardName());
            match.getMatchPhases().push(coof);
            coof.setOnComplete(() -> match.getDiscardedContainer().add(match.getDealerSide().getChildren().removeFirst(), Alignment.centerAbsolute(), false));
        } else if (!match.getUserSide().getChildren().isEmpty()) {
            CardOutOfField coof = new CardOutOfField((Card) match.getUserSide().getChildren().getFirst(), match);
            match.getUserDeck().discard(((Card) match.getUserSide().getChildren().getFirst()).getCardName());
            match.getMatchPhases().push(coof);
            coof.setOnComplete(() -> match.getDiscardedContainer().add(match.getUserSide().getChildren().removeFirst(), Alignment.centerAbsolute(), false));
        } else {
            if (!match.getDiscardedContainer().getChildren().isEmpty()) {
                Card card = (Card) match.getDiscardedContainer().getChildren().getLast();
                match.getDiscardedContainer().clear();
                match.getDiscardedContainer().add(card, Alignment.centerAbsolute(), false);
            }
            match.getDealerSide().clear();
            match.getUserSide().clear();
            if (!checkGameEnd()) {
                match.getMatchPhases().clearStack();
                match.getMatchPhases().switchTo("initial");
            }
        }
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
            case 0: pushAnimation((int)(match.getUserMult() * calcBaseDamage(user)), "user", "base");
                break;
            case 1: pushAnimation(calcBoost(dealer), "user", "21");
                break;
            case 2: pushAnimation((int)(match.getDealerMult() * calcBaseDamage(dealer)), "dealer", "base");
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
                    pushAnimation((int)(match.getUserMult() * delta), "user", "base");
                } else {
                    pushAnimation((int)(match.getDealerMult() * delta), "dealer", "base");
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

    public OnlineMatch getMatch() {
        return match;
    }

    public ArrayList<Card> getEffects() {
        return activeEffects;
    }
}


