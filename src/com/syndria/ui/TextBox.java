package com.syndria.ui;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;

import java.awt.*;

public class TextBox extends Box {

    private Text text;
    private String label;
    private boolean hoverEffects;

    private Image textImage;

    private int color;

    private Vector textPosition;

    private Runnable click;

    public TextBox(String text, int fontSize, String fontFamily, int color, Vector relativeDimensions) {
        super(relativeDimensions);
        this.text = new Text(text, fontSize, color, fontFamily, Font.PLAIN);
        this.color = color;
        textImage = this.text.getImage();
        setSize(textImage.getSize());
        hoverEffects = false;
        textPosition = position.copy();
    }

    public TextBox(String text, int fontSize, String fontFamily, int color) {
        this(text, fontSize, fontFamily, color, new Vector(1, 1));
    }

    public TextBox(int text, int fontSize, String fontFamily, int color) {
        this(String.format("%d", text), fontSize, fontFamily, color, new Vector(1, 1));
    }

    public TextBox(String text, int fontSize, int color, Vector relativeDimensions){
         this(text, fontSize, "", color, relativeDimensions);
    }

    public TextBox(int text, int fontSize, int color, Vector relativeDimensions) {
        this(String.format("%d", text), fontSize, color, relativeDimensions);
    }

    public TextBox(String text, int fontSize, int color) {
        this(text, fontSize, color, new Vector(1, 1));
    }

    public TextBox(int text, int fontSize, int color) {
        this(String.format("%d", text), fontSize, color);
    }


    @Override
    public void draw(double alpha) {
        if (isHover() && showsHoverEffects()) {
            Syndria.gfx.drawRect(position, size, hovercolor);
        }
        Syndria.gfx.drawimage(textImage, textPosition, color);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (hover && click != null && Syndria.input.MousePressed(1)) {
            click.run();
        }
    }

    public void onClick(Runnable click) {
        this.click = click;
    }

    @Override
    public void onPositionChange() {
        onSizeChange();
    }

    @Override
    public void onSizeChange() {
        textPosition = Vector.sum(position, Vector.sub(size, textImage.getSize()).scale(0.5));
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String text) {
        this.text.setText(text);
        textImage = this.text.getImage();
        setSize(textImage.getSize());
    }

    public void setLabel(int val) {
        setLabel(String.format("%d", val));
    }

    public void setLabelColor(int color) {
        this.color = color;
        text.setColor(color);
        textImage = text.getImage();
    }

    public void scale(double s) {
        textImage.scale(s, s);
        size = textImage.getSize();
    }

    public void setHoverColor(int color) {
        this.hovercolor = color;
    }

    public boolean showsHoverEffects() {
        return hoverEffects;
    }

    public void setHoverEffects(boolean hoverEffects) {
        this.hoverEffects = hoverEffects;
    }
}
