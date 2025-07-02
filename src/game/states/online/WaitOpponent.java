package game.states.online;

import com.syndria.state.State;
import com.syndria.time.Timer;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.TextBox;
import com.syndria.ui.UIComponent;
import game.Palette;
import game.entities.Card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitOpponent extends State {
    private OnlineMatch match;
    private int draws;
    private int i;
    Timer t;

    private TextBox waitingOpponent;
    private boolean waiting;
    private FixedContainer base;
    private String message;

    public WaitOpponent(String label, OnlineMatch match) {
        this.label = label;
        this.match = match;
        waitingOpponent = new TextBox("L'avversario sta pescando..", 18, "Pixellari", Palette.PEACH);
        base = new FixedContainer();
        base.add(waitingOpponent, Alignment.centerAbsolute(), false);
        waiting = true;
    }

    @Override
    public void enter() {
        i = 0;
        t = new Timer(0.8);
        draws = 0;
        waiting = true;
        try {
            match.socket.setKeepAlive(true);
            match.socket.setSoTimeout(0);
            Thread.sleep(1);
            message = match.in.readLine();
            if (message.startsWith("DRAWS:")) {
                try {
                    draws = Integer.parseInt(message.split(":")[1]);
                    waiting = false;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format received");
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyData(String data) {
        String[] datas = (data.split("_ACE_"))[1].split("!");
        List<String> aces = new ArrayList<String>(Arrays.asList(datas));
        while (!aces.isEmpty()) {
            for (UIComponent card : match.getDealerSide().getChildren()) {
                if (aces.isEmpty()) return;
                String name = aces.getFirst().split(":")[0];
                int index = Integer.parseInt(aces.getFirst().split(":")[1]);
                if (((Card) card).getCardName().equals(name)) {
                    ((Card) card).pickValue(index);
                    aces.removeFirst();
                }
            }
        }
    }

    @Override
    public void draw(double alpha) {
        if (waiting) {
            base.draw(alpha);
        }
    }

    @Override
    public void update(double dt) {
        if (i < draws) {
            match.drawCard("dealer");
            i++;
        } else {
            t.wait(dt);
            applyData(message);
            t.atEndTime(() -> {
                match.getMatchPhases().switchTo("damageCalc");
            });
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
