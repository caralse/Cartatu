package game.states.match;

import com.syndria.Syndria;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;
import com.syndria.time.Tween2D;
import game.Palette;

public class DamageAnimation extends StateAnimation {

    private String side;
    private int amount;
    private String type;
    private Tween2D tween;
    private Text damage;
    private Vector position;
    private Match match;

    public DamageAnimation(String side, int amount, String type, Match match) {
        this.side = side;
        this.amount = amount;
        this.type = type;
        this.match = match;
        Vector start; Vector end;

        if (amount > 0) {
            this.type = "heal";
        }

        if (side.equals("user")) {
            start = new Vector(0, 302);
            end = new Vector(90, 302);
        } else {
            start = new Vector(720, 172);
            end = new Vector(616, 172);
        }
        tween = new Tween2D(start, end, 0.3);
        damage = new Text(amount, 24, Palette.WHITE);
    }

    @Override
    public void enter() {
        if (type.equals("base")) {
            damage.setColor(Palette.MAGENTA);
            match.getMatchAudio().quickPlay("swoosh");
        } else if (type.equals("21")){
            damage.setColor(Palette.YELLOW);
            match.getMatchAudio().quickPlay("slash");
        } else if (type.equals("heal")) {
            damage.setColor(Palette.LIGHT_GREEN);
            match.getMatchAudio().play("heal");
        }
    }

    @Override
    public void update(double dt) {
        position = tween.update(dt);
        if (tween.isComplete()) {
            if (onComplete != null) {
                onComplete.run();
            }
            match.getMatchPhases().pop();
        }
    }

    @Override
    public void draw(double alpha) {
        Syndria.gfx.drawText(damage, position);
    }

}
