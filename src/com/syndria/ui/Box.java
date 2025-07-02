package com.syndria.ui;

import com.syndria.Syndria;
import com.syndria.math.Vector;

public class Box extends UIComponent{

    protected int bgcolor = 0xFFFF0000;
    protected int hovercolor = 0xFFFFF000;

    public Box() {
        super();
    }

    public Box(Vector relativeDimensions) {
        super();
        this.relativeDimensions = relativeDimensions;
    }

    @Override
    public void draw(double alpha) {
        if (this.hover) {
            Syndria.gfx.drawRect((int)this.position.getX(), (int)this.position.getY(), (int)this.size.getX(), (int)this.size.getY(), this.hovercolor);
        } else {
            Syndria.gfx.drawRect((int)this.position.getX(), (int)this.position.getY(), (int)this.size.getX(), (int)this.size.getY(), this.bgcolor);
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }
}
