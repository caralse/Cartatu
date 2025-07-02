package game.states.menu;

import game.entities.CardDex;
import game.entities.ShallowCard;

import java.util.ArrayList;
import java.util.Random;

public interface Packet {
    public ArrayList<ShallowCard> open();

    static Packet BRONZE() {
        Random rnd = new Random();
        final ArrayList<ShallowCard> cards = new ArrayList<>();
        return () -> {
            for (int i = 0; i < 4; i++) {
                cards.add(CardDex.getRandom());
            }
            if (rnd.nextDouble() < 0.35) {
                cards.add(CardDex.getRandomAce(false));
            } else {
                cards.add(CardDex.getRandom());
            }
            return cards;
        };
    }

    static Packet SILVER() {
        Random rnd = new Random();
        final ArrayList<ShallowCard> cards = new ArrayList<>();
        return () -> {
            for (int i = 0; i < 2; i++) {
                cards.add(CardDex.getRandom());
            }
            for (int i = 0; i < 2; i++) {
                cards.add(CardDex.getRandomAce(false));
            }
            double rndD = rnd.nextDouble();
            if (rndD < 0.2) {
                cards.add(CardDex.getRandomAce(true));
            } else if (rndD < 0.25){
                cards.add(CardDex.getRandomBoost());
            } else {
                cards.add(CardDex.getRandomAce(false));
            }
            return cards;
        };
    }

    static Packet GOLD() {
        Random rnd = new Random();
        final ArrayList<ShallowCard> cards = new ArrayList<>();
        return () -> {
            cards.add(CardDex.getRandom());

            for (int i = 0; i < 2; i++) {
                cards.add(CardDex.getRandomAce(false));
            }

            cards.add(CardDex.getRandomAce(true));

            for (int i = 0; i < 2; i++) {
                double rndD = rnd.nextDouble();
                if (rndD < 0.35) {
                    cards.add(CardDex.getRandomAce(true));
                } else if (rndD < 0.65) {
                    cards.add(CardDex.getRandomBoost());
                } else {
                    cards.add(CardDex.getRandomAce(false));
                }
            }
            return cards;
        };
    }
}
