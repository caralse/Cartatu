package game.entities;

import com.syndria.ui.Alignment;
import com.syndria.ui.UIComponent;
import game.states.match.CardOutOfField;
import game.states.match.DamageAnimation;
import game.states.match.DamageCalculator;
import game.states.match.Match;
import game.states.match.fx.Gek;
import game.states.match.fx.ModifyScore;
import game.states.online.OnlineDamageCalculator;

import java.util.Random;

public interface CardEffect {
    void applyEffect(DamageCalculator dmgCalc, String side);

    static CardEffect ape() {
        return (dmgCalc, side) -> {
            int amount = 5;
            DamageAnimation healing = new DamageAnimation(side, amount, "heal", dmgCalc.getMatch());
            dmgCalc.getMatch().getMatchPhases().push(healing);
            healing.setOnComplete(() -> {
                dmgCalc.getMatch().getHealthBar(side).addValue(amount);
            });
        };
    }

    static CardEffect ziska() {
        return (dmgCalc, side) -> {
            int amount;
            String receiver;

            if (side.equals("user")) {
                receiver = "dealer";
                amount = DamageCalculator.calcBaseDamage(dmgCalc.getMatch().getUserScoreValue());
                dmgCalc.getMatch().setUserMult(0d);
            } else if (side.equals("dealer")) {
                receiver = "user";
                amount = DamageCalculator.calcBaseDamage(dmgCalc.getMatch().getDealerScoreValue());
                dmgCalc.getMatch().setDealerMult(0d);
            } else throw new RuntimeException();

            DamageAnimation dmg = new DamageAnimation(receiver, -amount, "base", dmgCalc.getMatch());
            dmgCalc.getMatch().getMatchPhases().push(dmg);
            dmg.setOnComplete(() -> {
                dmgCalc.getMatch().getHealthBar(receiver).addValue(-amount);
            });
        };
    }

    static CardEffect lara() {
        return (dmgCalc, side) -> {
            if (side.equals("user")) {
                ModifyScore MS = new ModifyScore(dmgCalc.getMatch(), "bonus", "user");
                dmgCalc.getMatch().getMatchPhases().push(MS);
                MS.setOnComplete(() -> {
                    dmgCalc.getMatch().getUserSide().getChildren().forEach((card)-> {
                        CardValue val = ((Card)card).getValueObj();
                        if (val instanceof MultiValue) {
                            ((MultiValue) val).changeValuesBy(2);
                        } else {
                            ((SingleValue)val).changeValue(val.getValue()*2d);
                        }
                    });
                    dmgCalc.getMatch().calculateScore();
                });
            } else if (side.equals("dealer")) {
                ModifyScore MS = new ModifyScore(dmgCalc.getMatch(), "bonus", "dealer");
                dmgCalc.getMatch().getMatchPhases().push(MS);
                MS.setOnComplete(() -> {
                    dmgCalc.getMatch().getDealerSide().getChildren().forEach((card)-> {
                        CardValue val = ((Card)card).getValueObj();
                        if (val instanceof MultiValue) {
                            ((MultiValue) val).changeValuesBy(2);
                        } else {
                            ((SingleValue)val).changeValue(val.getValue()*2d);
                        }
                    });
                    dmgCalc.getMatch().calculateScore();
                });
            } else throw new RuntimeException();
        };
    }

    static CardEffect matti() {
        return (dmgCalc, side) -> {
            int amount = -5 * dmgCalc.getMatch().getBoostCardsOnField();
            DamageAnimation dmg = new DamageAnimation(side.equals("user") ? "dealer" : "user", amount, "21", dmgCalc.getMatch());
            dmgCalc.getMatch().getMatchPhases().push(dmg);
            dmg.setOnComplete(() -> {
                dmgCalc.getMatch().getHealthBar(side.equals("user") ? "dealer" : "user").addValue(amount);
            });
        };
    }

    static CardEffect nina() {
        return (dmgCalc, side) -> {
            if (side.equals("user")) {
                ModifyScore MS = new ModifyScore(dmgCalc.getMatch(), "malus", "dealer");
                dmgCalc.getMatch().getMatchPhases().push(MS);
                MS.setOnComplete(() -> {
                    dmgCalc.getMatch().getDealerSide().getChildren().forEach((card)-> {
                        CardValue val = ((Card)card).getValueObj();
                        if (val instanceof MultiValue) {
                            ((MultiValue) val).changeValuesBy(0.5);
                        } else {
                            ((SingleValue)val).changeValue(val.getValue()/2d);
                        }
                    });
                    dmgCalc.getMatch().calculateScore();
                });
            } else if (side.equals("dealer")) {
                ModifyScore MS = new ModifyScore(dmgCalc.getMatch(), "malus", "user");
                dmgCalc.getMatch().getMatchPhases().push(MS);
                MS.setOnComplete(() -> {
                    dmgCalc.getMatch().getUserSide().getChildren().forEach((card)-> {
                        CardValue val = ((Card)card).getValueObj();
                        if (val instanceof MultiValue) {
                            ((MultiValue) val).changeValuesBy(0.5);
                        } else {
                            ((SingleValue)val).changeValue(val.getValue()/2d);
                        }
                    });
                    dmgCalc.getMatch().calculateScore();
                });
            } else throw new RuntimeException();
        };
    }

    static CardEffect kevin() {
        return (dmgCalc, side) -> {
            Random rand = new Random();
            Card card;
            if (side.equals("user")) {
                int size = dmgCalc.getMatch().getDealerCards().size();
                card = (Card)(dmgCalc.getMatch().getDealerCards().get(rand.nextInt(size)));
                dmgCalc.getMatch().getDealerCards().remove(card);
                CardOutOfField coof = new CardOutOfField(card, dmgCalc.getMatch());
                dmgCalc.getMatch().getMatchPhases().push(coof);
                coof.setOnComplete(() -> {
                    dmgCalc.getMatch().getDealerSide().getChildren().remove(card);
                    dmgCalc.getMatch().getDealerSideBoost().getChildren().remove(card);
                    dmgCalc.getDealerEffects().remove(card);
                    if (dmgCalc instanceof OnlineDamageCalculator) {
                        ((OnlineDamageCalculator)dmgCalc).getEffects().remove(card);
                    }
                    Card card2 = (Card)(dmgCalc.getMatch().getDealerCards().get(rand.nextInt(size - 1)));
                    CardOutOfField coof2 = new CardOutOfField(card2, dmgCalc.getMatch());
                    dmgCalc.getMatch().getMatchPhases().push(coof2);
                    dmgCalc.getMatch().calculateScore();
                    coof2.setOnComplete(() -> {
                        dmgCalc.getMatch().getDealerSide().getChildren().remove(card2);
                        dmgCalc.getMatch().getDealerSideBoost().getChildren().remove(card2);
                        dmgCalc.getDealerEffects().remove(card2);
                        dmgCalc.getMatch().calculateScore();
                    });
                });
            } else if (side.equals("dealer")) {
                int size = dmgCalc.getMatch().getUserCards().size();
                card = (Card)(dmgCalc.getMatch().getUserCards().get(rand.nextInt(size)));
                dmgCalc.getMatch().getUserCards().remove(card);
                CardOutOfField coof = new CardOutOfField(card, dmgCalc.getMatch());
                dmgCalc.getMatch().getMatchPhases().push(coof);
                coof.setOnComplete(() -> {
                    dmgCalc.getMatch().getUserSide().getChildren().remove(card);
                    dmgCalc.getMatch().getUserSideBoost().getChildren().remove(card);
                    dmgCalc.getUserEffects().remove(card);
                    if (dmgCalc instanceof OnlineDamageCalculator) {
                        ((OnlineDamageCalculator)dmgCalc).getEffects().remove(card);
                    }
                    Card card2 = (Card)(dmgCalc.getMatch().getUserCards().get(rand.nextInt(size - 1)));
                    CardOutOfField coof2 = new CardOutOfField(card2, dmgCalc.getMatch());
                    dmgCalc.getMatch().getMatchPhases().push(coof2);
                    dmgCalc.getMatch().calculateScore();
                    coof2.setOnComplete(() -> {
                        dmgCalc.getMatch().getUserSide().getChildren().remove(card2);
                        dmgCalc.getMatch().getUserSideBoost().getChildren().remove(card2);
                        dmgCalc.getUserEffects().remove(card2);
                        dmgCalc.getMatch().calculateScore();
                    });
                });
            } else throw new RuntimeException();
        };
    }

    static CardEffect giovi() {
        return (dmgCalc, side) -> {
            if (side.equals("user")) {
                for (UIComponent card : dmgCalc.getMatch().getDealerSide().getChildren()) {
                    if (((Card)card).getCardType().equals("ace")) {
                        CardOutOfField coof = new CardOutOfField((Card) card, dmgCalc.getMatch());
                        dmgCalc.getMatch().getMatchPhases().push(coof);
                        coof.setOnComplete(() -> {
                            dmgCalc.getMatch().getDealerSide().getChildren().remove(card);
                            dmgCalc.getMatch().calculateScore();
                        });
                    }
                }
            } else if (side.equals("dealer")) {
                for (UIComponent card : dmgCalc.getMatch().getUserSide().getChildren()) {
                    if (((Card)card).getCardType().equals("ace")) {
                        CardOutOfField coof = new CardOutOfField((Card) card, dmgCalc.getMatch());
                        dmgCalc.getMatch().getMatchPhases().push(coof);
                        coof.setOnComplete(() -> {
                            dmgCalc.getMatch().getUserSide().getChildren().remove(card);
                            dmgCalc.getMatch().calculateScore();
                        });
                    }
                }
            } else throw new RuntimeException();
        };
    }

    static CardEffect gek() {
        return (dmgCalc, side) -> {
            Gek bomb = new Gek(dmgCalc.getMatch());
            dmgCalc.getMatch().getMatchPhases().push(bomb);
            if (dmgCalc instanceof OnlineDamageCalculator) {
                ((OnlineDamageCalculator)dmgCalc).getEffects().clear();
            } else {
                dmgCalc.getUserEffects().clear();
                dmgCalc.getDealerEffects().clear();
            }
            bomb.setOnComplete(() -> {
                if (!dmgCalc.getMatch().getUserSide().getChildren().isEmpty()) {
                    dmgCalc.getMatch().getDiscardedContainer().add(dmgCalc.getMatch().getUserSide().getChildren().removeFirst(),
                            Alignment.centerAbsolute(), false);
                }
                dmgCalc.getMatch().getUserSide().clear();
                dmgCalc.getMatch().getDealerSide().clear();
                dmgCalc.getMatch().getDealerSideBoost().clear();
                dmgCalc.getMatch().getUserSideBoost().clear();
                dmgCalc.getMatch().calculateScore();
            });
        };
    }

    static CardEffect nico() {
        return (dmgCalc, side) -> {
            int user = dmgCalc.getMatch().getHealthBar("user").getValue();
            int dealer = dmgCalc.getMatch().getHealthBar("dealer").getValue();
            int total = (dealer + user) / 2;
            DamageAnimation healing = new DamageAnimation("user", total-user, "base", dmgCalc.getMatch());
            dmgCalc.getMatch().getMatchPhases().push(healing);
            healing.setOnComplete(() -> {
                dmgCalc.getMatch().getHealthBar("user").setValue(total);
            });
            healing = new DamageAnimation("dealer", total-dealer, "base", dmgCalc.getMatch());
            dmgCalc.getMatch().getMatchPhases().push(healing);
            healing.setOnComplete(() -> {
                dmgCalc.getMatch().getHealthBar("dealer").setValue(total);
            });

        };
    }

}

//static CardEffect name() {
//    return (dmgCalc, side) -> {
//        if (side.equals("user")) {
//
//        } else if (side.equals("dealer")) {
//
//        } else throw new RuntimeException();
//    };
//}