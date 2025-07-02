package game.entities;

import com.syndria.Syndria;
import com.syndria.core.AudioManager;
import com.syndria.gfx.Image;
import com.syndria.math.Vector;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.TextBox;
import com.syndria.ui.UIComponent;

import java.util.Objects;

public class HealthBar extends UIComponent {

    private Image base;
    private Image frame;
    private int value;
    private final static double maxHealth = 100d;
    private String side;
    private int color;
    private int textColor = 0xFFFFF1E8;

    private double remainingPercentage;
    private double barLength;

    private TextBox text;
    private FixedContainer textBox;

    private Vector remainingHPPos;

    private AudioManager AM;

    public HealthBar(String side) {
        super();
        this.side = side;
        base = new Image("/gameResources/gfx/match/" + side + "Health.png");
        frame = new Image("/gameResources/gfx/match/" + side + "HealthFrame.png");
        size = base.getSize();

        if (Objects.equals(side, "user")) {
            position = new Vector(0, 280);
            remainingHPPos = new Vector(0, 282);
            textBox = new FixedContainer(0, 302, 24, 6);
        } else {
            position = new Vector(696, 0);
            remainingHPPos = new Vector(696, -1);
            textBox = new FixedContainer(696, 172, 24, 6);
        }
        text = new TextBox(100, 9, textColor);
        setValue(100);
    }

    @Override
    public void draw(double alpha) {
        base.draw(position);

        if (Objects.equals(side, "user")) {
            Syndria.gfx.drawRect(Vector.sum(remainingHPPos, new Vector(1, 198 - barLength)), new Vector(size.getX()-1, barLength), color);
        } else {
            Syndria.gfx.drawRect(remainingHPPos, new Vector(size.getX(), barLength), color);
        }

        frame.draw(position);
        textBox.draw(alpha);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        textBox.update(dt);
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }

    public int getValue() {
        return value;
    }

    public void setAM(AudioManager AM) {
        this.AM = AM;
    }

    public void setValue(int value) {
        this.value = value;
        remainingPercentage = value / maxHealth;
        barLength = Math.max(2, Math.min(199 * remainingPercentage, 199));
        color = remainingPercentage > 0.25 ? 0xFF29ADFF : 0xFFFF004D;
        textColor = remainingPercentage > 0.25 ? 0xFFFFF1E8 : 0xFFFF004D;
        if (AM != null) {
            if (AM.isPlaying("match") && remainingPercentage <= 0.45) {
                AM.stop("match");
                AM.play("lowHealth", true);
            } else if(AM.isPlaying("lowHealth") && remainingPercentage > 0.45) {
                AM.stop("lowHealth");
                AM.play("match", true);
            }
        }
        text.setLabel(value);
        text.setLabelColor(textColor);
        textBox.clear();
        textBox.add(text, Alignment.centerAbsolute(), false);
        text.onSizeChange();
    }

    public void addValue(int v) {
        this.value = Math.min(Math.max(0, this.value + v), 120);
        setValue(this.value);
    }
}
