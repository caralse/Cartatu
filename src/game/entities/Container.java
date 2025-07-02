package game.entities;

import com.syndria.Syndria;
import com.syndria.math.Vector;
import com.syndria.ui.UIComponent;

import java.util.ArrayList;

public class Container {
    private Vector position;
    private Vector size;

    private boolean bgVisible;
    private int bgColor;

    private final ArrayList<UIComponent> children = new ArrayList<>();

    public Container(int x, int y, int w, int h) {
        position = new Vector(x, y);
        size = new Vector(w, h);
        bgVisible = false;
        bgColor = 0xFFFFFFFF;
    }

    public void update(double dt) {
        for (UIComponent child : children) {
            child.update(dt);
        }
    }

    public void draw(double alpha) {
        if (bgVisible) {
            Syndria.gfx.drawRect(position, size, bgColor);
        }
        for (UIComponent child : children) {
            child.draw(alpha);
        }
    }

    public void add(UIComponent child) {
        child.setPosition(Vector.sum(position, new Vector(child.getMargin().getLeft(), child.getMargin().getTop())));
        children.add(child);
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        Vector delta = Vector.sub(position, this.position);
        this.position = position;
        for (UIComponent child : children) {
            Vector newPos = Vector.sum(child.getPosition(), delta);
            child.setPosition(newPos);
        }
    }

    public Vector getSize() {
        return size;
    }

    public void setSize(Vector size) {
        this.size = size;
    }

    public void setBgVisible(boolean bgVisible){
        this.bgVisible = bgVisible;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public ArrayList<UIComponent> getChildren() {
        return children;
    }
}
