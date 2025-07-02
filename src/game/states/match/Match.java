package game.states.match;

import com.syndria.core.AudioManager;
import com.syndria.gfx.Image;
import com.syndria.state.State;
import com.syndria.state.StateManager;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.TextBox;
import com.syndria.ui.UIComponent;
import game.entities.*;
import game.states.menu.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Match extends State {
    protected final StateManager s;
    protected Image background;

    private Dealer dealer;

    protected final StateManager matchPhases;
    protected final AudioManager matchAudio;

    protected Deck dealerDeck;
    protected Deck userDeck;
    protected Score dealerScore;
    protected Score userScore;
    protected TextBox dealerRemainingCards;
    protected TextBox userRemainingCards;
    public HealthBar dealerHealthBar;
    public HealthBar userHealthBar;

    protected final FixedContainer layout = new FixedContainer();
    protected final FixedContainer dealerSideBoost = new FixedContainer(161, 10, 400, 108);
    protected final FixedContainer dealerSide = new FixedContainer(161, 121, 400, 108);
    protected final FixedContainer dealerScoreContainer = new FixedContainer(557, 121, 141, 108);
    protected final FixedContainer dealerDeckContainer = new FixedContainer(0, 10, 164, 112);
    protected final FixedContainer userSide = new FixedContainer(161, 251, 400, 108);
    protected final FixedContainer userSideBoost = new FixedContainer(161, 362, 400, 108);
    protected final FixedContainer userScoreContainer = new FixedContainer(23, 251, 141, 108);
    protected final FixedContainer userDeckContainer = new FixedContainer(557, 362, 164, 112);

    protected FixedContainer discardedContainer;

    protected final HashMap<String, Card> matchCards = new HashMap<>();

    protected Double userMult = 1d;
    protected Double dealerMult = 1d;

    protected final Menu menu;

    public Match(String label, StateManager s, Menu menu) {
        super(label);
        this.s = s;
        this.menu = menu;
        background = new Image("/gameResources/gfx/match/matchBG.png");
        matchPhases = new StateManager();
        matchAudio = new AudioManager();

        discardedContainer = new FixedContainer(44, 131, 75, 108);

        userSide.setBgVisible(true);
        userSideBoost.setBgVisible(true);
        dealerSide.setBgVisible(true);
        dealerSideBoost.setBgVisible(true);

        layout.add(dealerScoreContainer, Alignment.none(), false);
        layout.add(userSideBoost, Alignment.none(), false);
        layout.add(userSide, Alignment.none(), false);
        layout.add(userScoreContainer, Alignment.none(), false);
        layout.add(dealerDeckContainer, Alignment.none(), false);
        layout.add(userDeckContainer, Alignment.none(), false);
        discardedContainer = new FixedContainer(44, 131, 75, 108);
        layout.add(discardedContainer, Alignment.none(), false);


        matchPhases.add(new InitialRound("initial", this));
        matchPhases.add(new Round("round", this));
        matchPhases.add(new DealerTurn("dealerTurn", this));
        matchPhases.add(new DamageCalculator("damageCalc", this));
        matchPhases.add(new EndMatch("endMatch", this));
    }

    @Override
    public void enter() {
        menu.getAudioManager().stop();

        User user = User.getInstance();

        dealer = new Dealer(SelectedDealer.getInstance().getDealer());

        matchAudio.loadSound("lowHealth", "audio/music/lowHealth.wav");
        if (SelectedDealer.getInstance().getDealer() == SelectedDealer.MAX) {
            matchAudio.loadSound("match", "audio/music/boss.wav");
        } else if (SelectedDealer.getInstance().getDealer() == SelectedDealer.MAX-1) {
            matchAudio.loadSound("match", "audio/music/fifth.wav");
        } else {
            matchAudio.loadSound("match", "audio/music/match.wav");
        }

        matchAudio.loadSound("win", "audio/sounds/win.wav");
        matchAudio.loadSound("endloop", "audio/music/endloop.wav");
        matchAudio.loadSound("drawCard", "audio/sounds/draw.wav");
        matchAudio.loadSound("pop", "audio/sounds/pop.wav");
        matchAudio.loadSound("slash", "audio/sounds/slash.wav");
        matchAudio.loadSound("swoosh", "audio/sounds/swoosh.wav");
        matchAudio.loadSound("heal", "audio/sounds/heal.wav");
        matchAudio.loadSound("applyEffect", "audio/sounds/applyEffect.wav");
        matchAudio.loadSound("bomb", "audio/sounds/bomb.wav");
        matchAudio.loadSound("buzz", "audio/sounds/buzz.wav");

        dealerDeck = dealer.getDeck();
        userDeck = user.getDeck();

        Card.populateHashMap(matchCards, dealerDeck);
        Card.populateHashMap(matchCards, userDeck);

        userDeck.shuffle();
        dealerDeck.shuffle();

        dealerHealthBar = new HealthBar("dealer");
        userHealthBar = new HealthBar("user");
        userHealthBar.setAM(matchAudio);

        dealerRemainingCards = new TextBox(dealerDeck.getRemainingCards(), 12, 0xFFFFF1E8);
        userRemainingCards = new TextBox(String.format("%d", userDeck.getRemainingCards()), 12, 0xFFFFF1E8);
        userScore = new Score();
        dealerScore = new Score();

        dealerDeckContainer.add(dealerDeck, Alignment.centerAbsolute(), false);
        dealerDeckContainer.add(dealerRemainingCards, Alignment.centerAbsolute(), false);
        userDeckContainer.add(userDeck, Alignment.centerAbsolute(), false);
        userDeckContainer.add(userRemainingCards, Alignment.centerAbsolute(), false);

        dealerScoreContainer.clear();
        dealerScoreContainer.add(dealerScore, Alignment.centerAbsolute(), false);
        userScoreContainer.clear();
        userScoreContainer.add(userScore, Alignment.centerAbsolute(), false);

        matchAudio.loop("match");
        matchPhases.switchTo("initial");
    }

    @Override
    public void draw(double alpha) {
        background.draw(0, 0);
        layout.draw(alpha);

        dealerSide.draw(alpha); //NOT ADDED TO LAYOUT
        dealerSideBoost.draw(alpha); // ONLY TO DRAW; NO UPDATES

        userHealthBar.draw(alpha);
        dealerHealthBar.draw(alpha);
        matchPhases.draw(alpha);
    }

    @Override
    public void update(double dt) {
        matchPhases.update(dt);
        layout.update(dt);
        userSide.update(dt);
    }

    public void calculateScore() {
        double usr = 0; double dlr = 0;

        for (UIComponent card : userSide.getChildren()) {
            usr += ((Card) card).getValue(); // Cast to Card and call getValue()
        }
        for (UIComponent card : dealerSide.getChildren()) {
            dlr += ((Card)card).getValue();
        }

        userScore.setValue((int)usr);
        dealerScore.setValue((int)dlr);

        if (userScore.hasIncreased()) {
            userScoreContainer.clear();
            userScoreContainer.add(userScore, Alignment.centerAbsolute(), false);
        }
        if (dealerScore.hasIncreased()) {
            dealerScoreContainer.clear();
            dealerScoreContainer.add(dealerScore, Alignment.centerAbsolute(), false);
        }

    }

    public StateManager getMatchPhases() {
        return matchPhases;
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }

    public int getUserScoreValue() {
        return userScore.getValue();
    }
    public Score getUserScore() {
        return userScore;
    }

    public int getDealerScoreValue() {
        return dealerScore.getValue();
    }
    public Score getDealerScore() {
        return dealerScore;
    }

    public void setUserScore(int v) {
        userScore.setValue(v);
    }

    public void setDealerScore(int v) {
        dealerScore.setValue(v);
    }

    protected void updateUserRemainingCards(int rc) {
        userRemainingCards.setLabel(String.format("%d", rc));
    }

    protected void updateDealerRemainingCards(int rc) {
        dealerRemainingCards.setLabel(String.format("%d", rc));
    }

    public AudioManager getMatchAudio() {
        return matchAudio;
    }

    public void clearMatchCards() {
        matchCards.clear();
        System.gc();
    }

    public HealthBar getHealthBar(String side) {
        if (side.equals("user")) return userHealthBar;
        if (side.equals("dealer")) return dealerHealthBar;
        else throw new RuntimeException();
    }

    public void drawCard(String side) {
        Card card;
        String draw;
        if (side.equals("dealer")) {
            draw = dealerDeck.drawCard();
            if (draw == null) {
                dealerDeck.restore();
                DamageAnimation dmg = new DamageAnimation("dealer", -20, "21", this);
                dmg.setOnComplete(() -> {
                    dealerHealthBar.addValue(-20);
                });
                matchPhases.push(dmg);
                card = matchCards.get(dealerDeck.drawCard()).copy();
            } else {
                card = matchCards.get(draw).copy();
            }
        } else if (side.equals("user")) {
            draw = userDeck.drawCard();
            if (draw == null) {
                userDeck.restore();
                DamageAnimation dmg = new DamageAnimation("user", -20, "21", this);
                dmg.setOnComplete(() -> {
                    userHealthBar.addValue(-20);
                });
                matchPhases.push(dmg);
                card = matchCards.get(userDeck.drawCard()).copy();
            } else {
                card = matchCards.get(draw).copy();
            }
        } else {
            throw new RuntimeException();
        }
        if (userHealthBar.getValue() == 0 || dealerHealthBar.getValue() == 0) {
            matchPhases.switchTo("endMatch");
            return;
        }
        CardDrawnAnimation cda = new CardDrawnAnimation(this, side, 0.4);
        matchPhases.push(cda);
        cda.setOnComplete(() -> {
            if (addCardToContainer(card, side, card.getCardType())) {
                CardOutOfField coof = new CardOutOfField(card, this);
                matchPhases.push(coof);
                coof.setOnComplete(() -> {
                    discardedContainer.clear();
                    discardedContainer.add(card, Alignment.centerAbsolute(), false);
                });
            }
            calculateScore();
        });
    }

    private boolean addCardToContainer(Card card, String side, String type) { //Return true when a card is discarded
        if (side.equals("dealer")) {
            if (type.equals("boost")) {
                if (dealerSideBoost.getChildren().size() < 5) {
                    dealerSideBoost.add(card, Alignment.inLine(), false);
                } else {
                    card.setPosition(dealerDeck.getPosition().copy());
                    discardedContainer.add(card, Alignment.none(), false);
                    dealerDeck.discard(card.getCardName());
                    return true;
                }
            } else {
                dealerSide.add(card, Alignment.inLine(), false);
            }
            updateDealerRemainingCards(dealerDeck.getRemainingCards());
        } else if (side.equals("user")) {
            if (type.equals("boost")) {
                if (userSideBoost.getChildren().size() < 5) {
                    userSideBoost.add(card, Alignment.inLine(), false);
                } else {
                    card.setPosition(userDeck.getPosition().copy());
                    discardedContainer.add(card, Alignment.none(), false);
                    userDeck.discard(card.getCardName());
                    return true;
                }
            } else {
                userSide.add(card, Alignment.inLine(), false);
            }
            updateUserRemainingCards(userDeck.getRemainingCards());
        } else {
            throw new RuntimeException();
        }
        return false;
    }

    public int getBoostCardsOnField() {
        return userSideBoost.getChildren().size() + dealerSideBoost.getChildren().size();
    }

    public List<UIComponent> getDealerCards() {
        List<UIComponent> combinedList = Stream.of(dealerSideBoost.getChildren(), dealerSide.getChildren())
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
        return combinedList;
    }

    public List<UIComponent> getUserCards() {
        List<UIComponent> combinedList = Stream.of(userSideBoost.getChildren(), userSide.getChildren())
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
        return combinedList;
    }

    public FixedContainer getDealerSide() {
        return dealerSide;
    }

    public FixedContainer getDealerSideBoost() {
        return dealerSideBoost;
    }

    public FixedContainer getUserSide() {
        return userSide;
    }

    public FixedContainer getUserSideBoost() {
        return userSideBoost;
    }

    public double getUserMult() {
        return userMult;
    }

    public void setUserMult(Double userMult) {
        this.userMult = userMult;
    }

    public double getDealerMult() {
        return dealerMult;
    }

    public void setDealerMult(Double dealerMult) {
        this.dealerMult = dealerMult;
    }

    public FixedContainer getDiscardedContainer() {
        return discardedContainer;
    }

    public StateManager getGamePhases() {
        return s;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public Deck getDealerDeck() {
        return dealerDeck;
    }

    public Deck getUserDeck() {
        return userDeck;
    }
}
