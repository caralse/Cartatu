package game.states.menu;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.math.Vector;
import com.syndria.time.Tween2D;
import com.syndria.ui.Picture;
import game.Palette;
import game.entities.ShallowCard;
import game.states.match.StateAnimation;

public class PackOpeningAnimation extends StateAnimation {

    private ShallowCard card;
    private Picture cardPic;

    private Tween2D tween;

    private Menu menu;

    private Image toDraw;

    public PackOpeningAnimation(ShallowCard card, Menu menu) {
        super();
        this.card = card;
    }

    @Override
    public void enter() {
        tween = new Tween2D(new Vector(1, 1), new Vector(2, 2), 2);
        toDraw = Image.scale(card.getImg(), new Vector(1, 1));
    }

    @Override
    public void draw(double alpha) {
        Syndria.gfx.drawimage(toDraw, Vector.sub(Syndria.gfx.getScreenSize(), toDraw.getSize()).scale(0.5), -1);
    }

    @Override
    public void update(double dt) {
        toDraw = Image.scale(card.getImg(), tween.update(dt));
        if (tween.isComplete() && onComplete != null) {
            onComplete.run();
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
