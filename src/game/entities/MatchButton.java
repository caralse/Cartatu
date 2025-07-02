package game.entities;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;
import com.syndria.ui.UIComponent;
import game.Palette;

public class MatchButton extends UIComponent {

    private String label;
    private Runnable click;
    private Text txt;
    private static final Image img = new Image("/gameResources/gfx/match/button.png");
    private static final Image shadow = new Image("/gameResources/gfx/match/buttonShadow.png");
    private Vector txtOffset;
    private Vector txtOffsetMove;
    private Vector shadowPosition;

    public MatchButton(String label, Vector position) {
        this.label = label;
        this.position = position;
        shadowPosition = position.copy();
        setSize(shadow.getSize());
        txt = new Text(label, 17, Palette.WHITE);
        txtOffset = Vector.sub(img.getSize(), txt.getDimension());
        txtOffset.scale(0.5);
        txtOffset.sum(position);
        txtOffset.sum(new Vector(0, -2));
        txtOffsetMove = txtOffset.copy();
    }

    @Override
    public void draw(double alpha) {
        shadow.draw(shadowPosition);
        img.draw(position);
        Syndria.gfx.drawText(txt, txtOffset);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (this.isHover() && Syndria.input.MouseReleased(1)) {
            position.addScalar(2);
            txtOffset.addScalar(2);
            if (click != null) {
                click.run();
            }
            txt.setColor(0xFF1d2b53);
        } else {
            position = shadowPosition.copy();
            txtOffset = txtOffsetMove.copy();
            txt.setColor(0xFFFFF1E8);
        }
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }

    public void onClick(Runnable click) {
        this.click = click;
    }

}
