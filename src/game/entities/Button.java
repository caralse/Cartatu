package game.entities;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;
import com.syndria.time.Tween2D;
import com.syndria.ui.UIComponent;
import game.Palette;

public class Button extends UIComponent {
    private String text;
    private Image button;
    private Image buttonShadow;
    private int color;
    private Text label;
    private Vector shadowPos;

    private Tween2D clickTween;
    private Vector endV;
    private boolean startTween;

    private Runnable click;

    public Button(String text, int fontSize) {
        super();
        this.text = text;
        button = new Image("/gameResources/gfx/menu/button.png");
        buttonShadow = new Image("/gameResources/gfx/menu/buttonShadow.png");
        size = button.getSize();
        color = -1;
        label = new Text(text, fontSize, Palette.WHITE);
        shadowPos = position.copy();
        endV = new Vector(2, 2);
        clickTween = new Tween2D(new Vector(0, 0), endV, 0.1);
        startTween = false;
    }

    public Button(String text) {
        this(text, 32);
    }

    @Override
    public void onPositionChange() {
        shadowPos = position.copy();
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (isHover() && Syndria.input.MousePressed(1)){
            startTween = true;
        }
        if (startTween) {
            clickTween.update(dt);
        }
        if (click != null && clickTween.isComplete()) {
            startTween = false;
            clickTween.reset();
            click.run();
        }
    }

    @Override
    public void draw(double alpha) {
        Syndria.gfx.drawimage(buttonShadow, shadowPos, -1);
        Syndria.gfx.drawimage(button,Vector.sum(position, clickTween.getVector()), color);
        Syndria.gfx.drawText(label, Vector.sum(position, Vector.sub(size, label.getDimension()).scale(0.5)));
    }

    @Override
    public void onSizeChange() {
        Vector scale = Vector.div(size, button.getSize());
        endV.scale(scale);
        buttonShadow.scale(scale);
        button.scale(scale);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void onClick(Runnable click) {
        this.click = click;
    }

    public Text getText() {
        return label;
    }

}
