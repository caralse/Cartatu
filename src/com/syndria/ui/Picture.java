package com.syndria.ui;

import com.syndria.gfx.Image;
import com.syndria.math.Vector;

public class Picture extends UIComponent {

    private Image image;
    private String path;

    public Picture(String path) {
        super();
        this.path = path;
        image = new Image(path);
        size = image.getSize();
    }

    @Override
    public void draw(double alpha) {
        image.draw(position);
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }

    public Image getImage() {
        return image;
    }

    public void scale(double x, double y) {
        image.scale(x, y);
        setSize(image.getSize());
    }

    public void scale(Vector scale) {
        image.scale(scale);
        setSize(image.getSize());
    }
}
