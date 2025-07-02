package com.syndria.ui;

import com.syndria.Syndria;
import com.syndria.math.Vector;

public abstract class UIComponent {
    private static int UICount = 0;
    private int ID;
    protected boolean hover = false;
    protected Vector position;
    protected Vector oldPosition;
    protected Vector size;
    private Vector oldSize;
    protected Spacing margin;
    protected Spacing padding;
    protected Vector relativeDimensions = new Vector(1, 1);
    private String label;

    protected boolean positionChange;
    protected boolean sizeChange;

    private Runnable click;

    public UIComponent(){
        position = new Vector(0, 0);
        oldPosition = position.copy();
        size = new Vector(0, 0);
        oldSize = size.copy();
        margin = new Spacing(0);
        padding = new Spacing(0);
        ID = UICount;
        UICount += 1;
        positionChange = false;
        sizeChange = false;
    }

    public abstract void draw(double alpha);
    public abstract void onPositionChange();
    public abstract void onSizeChange();

    public void update(double dt){
        if (this.isInBounds(Syndria.input.getMousePosition())) {
            this.hover = true;
        } else {
            this.hover = false;
        }
        if (!position.equals(oldPosition)) {
            onPositionChange();
            positionChange = true;
            oldPosition = position.copy();
        } else {
            positionChange = false;
        }
        if (!size.equals(oldSize)) {
            onSizeChange();
            sizeChange = true;
            oldSize = size.copy();
        } else {
            sizeChange = false;
        }
    }

    public boolean isInBounds(Vector v) {
        return  v.getX() > position.getX() && v.getX() < (position.getX() + size.getX()) &&
                v.getY() > position.getY() && v.getY() < (position.getY() + size.getY());
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position.copy();
    }

    public Vector getSize() {
        return size;
    }

    public void setSize(Vector size) {
        this.size = size;
    }
    public void setSize(double w, double h) {
        this.size = new Vector(w, h);
    }

    public Spacing getMargin() {
        return margin;
    }

    public void setMargin(Spacing margin) {
        this.margin = margin;
    }

    public Spacing getPadding() {
        return padding;
    }

    public void setPadding(Spacing padding) {
        this.padding = padding;
    }

    public Vector getRelativeDimensions() {
        return relativeDimensions;
    }

    public void setRelativeDimensions(Vector relativeDimensions) {
        this.relativeDimensions = relativeDimensions.copy();
    }

    public void setRelativeDimensions(double x, double y) {
        this.relativeDimensions = new Vector(x, y);
    }

    public int getID() { return this.ID; };

    public boolean isHover(){
        return this.hover;
    };

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
