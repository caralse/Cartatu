package game.states.online;

import com.syndria.state.StateManager;
import com.syndria.ui.Alignment;
import com.syndria.ui.TextBox;
import game.entities.*;
import game.states.match.*;
import game.states.menu.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OnlineMatch extends Match {

    private Dealer dealer;

    public final Socket socket;
    public final BufferedReader in;
    public final PrintWriter out;

    protected final HashMap<String, Card> matchCards = new HashMap<>();

    public OnlineMatch(String label, StateManager s, Menu menu, Socket socket, BufferedReader in, PrintWriter out){
        super(label, s, menu);
        this.socket = socket;
        this.in = in;
        this.out = out;
        matchPhases.add(new InitialRound("initial", this));
        matchPhases.add(new OnlineRound("round", this));
        matchPhases.add(new WaitOpponent("waitOpponent", this));
        matchPhases.add(new OnlineDamageCalculator("damageCalc", this));
        matchPhases.add(new EndMatch("endMatch", this));
    }

    @Override
    public void enter() {
        menu.getAudioManager().stop();

        matchAudio.loadSound("lowHealth", "audio/music/lowHealth.wav");
        matchAudio.loadSound("match", "audio/music/fifth.wav");

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

        dealerHealthBar = new HealthBar("dealer");
        userHealthBar = new HealthBar("user");
        userHealthBar.setAM(matchAudio);

        // SET DECKS TO START ONLINE MATCH
        User user = User.getInstance();
        userDeck = user.getDeck();
        userDeck.shuffle();
        Card.populateHashMap(matchCards, userDeck);
        try {
            socket.setKeepAlive(true);
            socket.setSoTimeout(30000);

            // Send deck
            String deckString = listToString(userDeck.getCards());
            String fullMessage = "DECK " + deckString + "END\n";  // explicit newline
            out.print(fullMessage);
            out.flush();

            String response = in.readLine();

            if (response != null && response.contains("ODECK: ")) {
                String listString = response.substring(7); // Remove "LIST " prefix
                ArrayList<String> receivedList = stringToList(listString);
                dealer = new Dealer(receivedList);
                dealerDeck = dealer.getDeck();
                Card.populateHashMap(matchCards, dealerDeck);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    public String listToString(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            sb.append(item).append(",");
        }
        // Remove last comma if exists
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public ArrayList<String> stringToList(String str) {
        ArrayList<String> list = new ArrayList<>();
        if (str == null || str.trim().isEmpty()) {
            return list;
        }

        String[] items = str.split(",");
        list.addAll(Arrays.asList(items));
        return list;
    }

    public String mapToString(HashMap<String, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            sb.append(entry.getKey())
                    .append(":")
                    .append(entry.getValue())
                    .append(",");
        }
        // Remove last comma if exists
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public void clearMatchCards() {
        matchCards.clear();
        System.gc();
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
            System.err.println("DrawCard Error");
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

    public Dealer getDealer() {
        return dealer;
    }

}
