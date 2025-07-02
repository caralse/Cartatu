package game.entities;

import com.syndria.Syndria;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;
import com.syndria.ui.UIComponent;
import game.Palette;

public class Score extends UIComponent {
    private int value;
    private Text txt;
    private boolean increased = false;

    public Score() {
        super();
        value = 0;
        txt = new Text(" ", 24, Palette.WHITE);
    }

    @Override
    public void draw(double alpha) {
        Syndria.gfx.drawText(txt, (int)position.getX(), (int)position.getY());
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }

    public void setValue(int value) {
        this.value = value;
        txt.setText(String.format("%d", value));
        increased = txt.getDimension().getX() > size.getX();

        this.setSize(txt.getDimension());
        if (value < 21) {
            txt.setColor(Palette.WHITE);
        } else if (value == 21) {
            txt.setColor(Palette.YELLOW);
        } else {
            txt.setColor(Palette.BORDEAUX);
        }
    }

    public Vector getSize() {
        return txt.getDimension();
    }

    public boolean hasIncreased() {
        return increased;
    }

    public int getValue() {
        return value;
    }
}
