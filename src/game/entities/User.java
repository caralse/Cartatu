package game.entities;

import java.io.*;
import java.util.HashMap;

public class User implements Serializable {

    private static User INSTANCE;
    private String username;
    private int coins;
    private int level;
    private HashMap<String, Integer> obtainedCards = new HashMap<>();
    private HashMap<String, Integer> currentDeck;
    private static final String FILE_NAME = "/.cartatu_userdata.dat";
    private static String FILE_PATH;

    private static File CONFIG_HOME;

    private User(String username) {
        this.username = username;
        String home = System.getenv("APPDATA");
        if (home == null) {
            home = System.getProperty("user.home");
        }
        CONFIG_HOME = new File(home, "/Cartatu").getAbsoluteFile();
        CONFIG_HOME.mkdirs();
        FILE_PATH = CONFIG_HOME.getAbsolutePath() + FILE_NAME;
    }

    public static User getInstance() {
        if (INSTANCE == null) {
            INSTANCE = loadOrCreate();
        }
        return INSTANCE;
    }

    public static User loadOrCreate() {
        // Attempt to load user data
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            // Create a new instance if loading fails
            return new User("defaultUser");
        }
    }

    public void init() {
        Deck deck = new Deck("standard.xml", "user");
        level = 1;
        coins = 0;
        obtainedCards.clear();
        for (String card : deck.getCards()) {
            obtainedCards.merge(card, 1, Integer::sum);
        }
        currentDeck = ((HashMap<String, Integer>) obtainedCards.clone());
        saveData();
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(INSTANCE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadUserData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            INSTANCE = (User) ois.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    public Deck getDeck() {
        return new Deck("user", currentDeck);
    }

    public HashMap<String, Integer> getDeckMap() {
        return currentDeck;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }

    public void incrementLevel(int value) {
        level = Math.min(SelectedDealer.MAX, level + value);
    }

    public int getCoins() {
        return coins;
    }

    public void incrementCoins(int value) {
        coins += value;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addCard(String card) {
        obtainedCards.merge(card, 1, Integer::sum);
    }

    public HashMap<String, Integer> getObtainedCards() {
        return obtainedCards;
    }

    public void setDeck(HashMap<String, Integer> deck) {
        currentDeck = (HashMap<String, Integer>) deck.clone();
    }
}
