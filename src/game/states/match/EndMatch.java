package game.states.match;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.time.Timer;
import com.syndria.time.Tween;
import com.syndria.time.Tween2D;
import game.Palette;
import game.entities.SelectedDealer;
import game.entities.User;

public class EndMatch extends State {

    Tween2D tween;
    Tween2D tween2;

    Vector pos;
    Vector dim;

    Timer pause;
    Tween fade;

    int color;

    boolean win;

    private Match match;

    private Image splashArt;

    int bonus; int total;
    private Timer t;

    public EndMatch(String label, Match match) {
        super(label);
        this.match = match;
    }

    @Override
    public void enter() {
        match.getMatchAudio().stop();
        t = new Timer(4.5);
        win = false;
        if (match.userHealthBar.getValue() == 0) {
            match.getMatchAudio().play("endloop", true);
            splashArt = new Image("/gameResources/gfx/lose.png");
        } else {
            match.getMatchAudio().play("win");
            t.atEndTime(() -> {
                match.getMatchAudio().play("endloop", true);
            });
            win = true;
            splashArt = new Image("/gameResources/gfx/win.png");
            bonus = match.getHealthBar("user").getValue() > 50 ? match.getDealer().getReward() / 2 : 0;
            User.getInstance().incrementCoins(match.getDealer().getReward() + bonus);
            if (User.getInstance().getLevel() == SelectedDealer.getInstance().getDealer()) {
                User.getInstance().incrementLevel(1);
            }
            User.getInstance().saveData();
        }

        match.getUserSideBoost().clear();
        match.getUserSide().clear();
        match.getDealerSideBoost().clear();
        match.getDealerSide().clear();
        match.getDiscardedContainer().clear();

        match.clearMatchCards();

        pos = Syndria.gfx.getScreenCenter();
        dim = new Vector(0, 0);
        tween = new Tween2D(pos, new Vector(0, 0), 0.7);
        tween2 = new Tween2D(dim, new Vector(720, 480), 0.7);
        pause = new Timer(0.3);
        fade = new Tween(0xFF000000, 0x00000000, 0.3);
        color = 0xFF000000;
    }

    @Override
    public void draw(double alpha) {
        if (pause.isComplete()) {
            splashArt.draw(0, 0);
            if (win) {
                Syndria.gfx.drawText(new Text(String.format("Vittoria: %d$", match.getDealer().getReward()), 24, Palette.GOLD), 228, 200);
                Syndria.gfx.drawText(new Text(String.format("Bonus: %d$", bonus), 24, Palette.GOLD), 228, 250);
                Syndria.gfx.drawText(new Text(String.format("Totale: %d$", bonus + match.getDealer().getReward()), 24, Palette.GOLD), 228, 300);
            }
        }
        Syndria.gfx.drawRect(pos, dim, color);
    }

    @Override
    public void update(double dt) {
        pos = tween.update(dt);
        dim = tween2.update(dt);
        t.wait(dt);
        if (tween.isComplete()) {
            pause.wait(dt);
            if (pause.isComplete()) {
                color = (int)fade.update(dt);
                if (fade.isComplete() && Syndria.input.MousePressed(1)) {
                    match.getMatchAudio().closeAll();
                    match.getGamePhases().setCurrentState("reloadLauncher");
                    match.getMatchPhases().clearStack();
                    splashArt.getImg().flush();
                    System.gc();
                }
            }
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
