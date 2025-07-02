package game.entities;

public class SelectedDealer {
    private static SelectedDealer INSTANCE;

    public final static int STANDARD = 1;
    public final static int SECOND = 2;
    public final static int THIRD = 3;
    public final static int FOURTH = 4;
    public final static int FIFTH = 5;
    public final static int SIXTH = 6;
    public final static int MAX = 6;

    private int dealer;

    private SelectedDealer() {
        this.dealer = 1;
    }

    public static SelectedDealer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SelectedDealer();
        }
        return INSTANCE;
    }

    public void setDealer(int d) {
        if (d > 6) throw new RuntimeException();
        dealer = d;
    }

    public int getDealer() {
        return dealer;
    }
}
