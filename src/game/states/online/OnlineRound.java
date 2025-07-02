package game.states.online;

import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.ui.UIComponent;
import game.Palette;
import game.entities.Button;
import game.entities.Card;
import game.states.match.Match;

import java.io.IOException;

public class OnlineRound extends State {

    private Match match;
    private Button drawButton;
    private Button standButton;
    private Button quitButton;

    private int draws;

    public OnlineRound(String label, OnlineMatch match) {
        super(label);
        this.match = match;
        drawButton = new Button("DRAW", 17);
        drawButton.setColor(Palette.LILLE);
        drawButton.setPosition(new Vector(603, 307));

        standButton = new Button("STAND", 17);
        standButton.setColor(Palette.LILLE);
        standButton.setPosition(new Vector(603, 273));

        quitButton = new Button("ESCI", 17);
        quitButton.setColor(Palette.BLUE);
        quitButton.setPosition(new Vector(53, 402));

        drawButton.onClick(() -> {
            if (match.getUserSide().getChildren().size() < 5) {
                match.drawCard("user");
                draws++;
            }
        });

        standButton.onClick(() -> {
            try {
                match.socket.setKeepAlive(true);
                match.socket.setSoTimeout(30000);
                String data = String.format("DRAWS:%d:", draws);
                data += collectAceValues();
                //data += collectEffects();
                match.out.write(data);
                match.out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            match.getMatchPhases().switchTo("waitOpponent");
        });

        quitButton.onClick(() -> {
            match.userHealthBar.setValue(0);
            match.getMatchPhases().switchTo("damageCalc");
            match.out.write("FORFAIT");
        });

    }

    @Override
    public void enter() {
        draws = 0;
    }

    @Override
    public void draw(double alpha) {
        drawButton.draw(alpha);
        standButton.draw(alpha);
        quitButton.draw(alpha);
    }

    @Override
    public void update(double dt) {
        drawButton.update(dt);
        standButton.update(dt);
        quitButton.update(dt);

        match.calculateScore();
    }

    private String collectAceValues() {
        String values = "_ACE_";
        for (UIComponent card : match.getUserSide().getChildren()) {
            if (((Card)card).getCardType().equals("ace")) {
                values += String.format("%s:%d!", ((Card)card).getCardName(), ((Card)card).getIndex());
            }
        }
        return values;
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
