package com.syndria.ui;

import jdk.jfr.Percentage;

public class Spacing {
    private int top;
    private int bottom;
    private int left;
    private int right;

    public Spacing(int top, int bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public Spacing(int horizontal, int vertical) {
        this(vertical, vertical, horizontal, horizontal);
    }

    public Spacing(int spacing) {
        this(spacing, spacing, spacing, spacing);
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getVertical() {
        return top + bottom;
    }

    public int getHorizontal() {
        return right + left;
    }

    @Override
    public String toString() {
        return String.format("top: %d, bot: %d, left: %d, right: %d", top, bottom, left, right);
    }
}
