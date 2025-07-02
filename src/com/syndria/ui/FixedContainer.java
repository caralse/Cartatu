package com.syndria.ui;

import java.util.ArrayList;
import java.util.List;

import com.syndria.Syndria;
import com.syndria.math.Vector;

public class FixedContainer extends UIComponent{

    private final List<UIComponent> children = new ArrayList<>();
    private Vector contentPlacement;
    private int bgColor = 0x08FFFFFF;
    private boolean bgVisible = false;
    private int lineBreak = 100;
    private final List<Alignment> alignments = new ArrayList<>();

    private Runnable click;

    public FixedContainer(Vector position, Vector size){
        super();
        this.position = position;
        this.size = size;
        contentPlacement = position;
    }

    public FixedContainer(int x, int y, int w, int h){
        super();
        position = new Vector(x, y);
        size = new Vector(w, h);
        contentPlacement = position;
    }

    public FixedContainer() {
        super();
        position = new Vector(0, 0);
        size = new Vector(Syndria.renderer.getWindow().getScreen().getWidth(), Syndria.renderer.getWindow().getScreen().getHeight());
        contentPlacement = new Vector(0, 0);
    }

    public void add(UIComponent child, Alignment a, boolean scale){
        if (scale) {
            child.setSize(Vector.scale(this.size, child.getRelativeDimensions()));
        }
        children.add(child);
        alignments.add(a);
        a.align(child, this);
    }

    public void clear() {
        children.clear();
        alignments.clear();
    }

    @Override
    public void draw(double alpha){
        if (this.bgVisible) {
            Syndria.gfx.drawRect(position, size, bgColor);
        }
        for (UIComponent child : children){
            child.draw(alpha);
        }
    }

    @Override
    public void onPositionChange() {
    }

    @Override
    public void onSizeChange() {

    }

    @Override
    public void update(double dt){
        super.update(dt);
        for (UIComponent child : children){
            child.update(dt);
        }
        if (isHover() && Syndria.input.MousePressed(1)) {
            if (click != null) {
                click.run();
            }
        }
    }

    @Override
    public void setPadding(Spacing spacing) {
        this.padding = spacing;
        contentPlacement = Vector.sum(position, new Vector(spacing.getLeft(), spacing.getTop()));
    }

    public int getBgColor() {
        return this.bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public boolean isBgVisible() {
        return bgVisible;
    }

    public void setBgVisible(boolean bgVisible) {
        this.bgVisible = bgVisible;
    }

    public Vector getContentPlacement() {
        return contentPlacement;
    }

    public void setContentPlacement(Vector contentPlacement) {
        this.contentPlacement = contentPlacement.copy();
    }

    public List<UIComponent> getChildren() {
        return this.children;
    }

    public void setLineBreak(int lineBreak) {
        this.lineBreak = lineBreak;
    }

    public int getLineBreak() {
        return lineBreak;
    }

    public void remove(UIComponent child) {
        children.remove(child);
    }

    public void onClick(Runnable click) {
        this.click = click;
    }
}
