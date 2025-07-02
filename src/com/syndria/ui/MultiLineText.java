package com.syndria.ui;

import com.syndria.Syndria;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MultiLineText extends UIComponent {

    private StringTokenizer TOKENS;
    private String text;
    private String fontFamily;
    private int fontSize;
    private int fontColor;

    private ArrayList<Text> boxes = new ArrayList<>();
    private ArrayList<Vector> positions = new ArrayList<>();

    public MultiLineText(String text, int fontSize, String fontFamily, int fontColor) {
        this.text = text;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.fontFamily = fontFamily;
        init();
    }

    public MultiLineText(String text, int fontSize, int fontColor) {
        this(text, fontSize, "Joystix Monospace", fontColor);
    }


    @Override
    public void draw(double alpha) {
        for (int i = 0; i < boxes.size(); i++) {
            Syndria.gfx.drawText(boxes.get(i), positions.get(i));
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);
    }

    @Override
    public void onPositionChange() {
        init();
    }

    @Override
    public void onSizeChange() {

    }

    private void init() {
        boxes.clear();
        positions.clear();
        double maxW = 0;
        double totalH = 0;
        TOKENS = new StringTokenizer(text);
        Vector pos = position.copy();
        while (TOKENS.hasMoreTokens()) {
            String t = TOKENS.nextToken("^");
            Text tbox = new Text(t, fontSize, 1, fontFamily, Font.PLAIN);
            tbox.setColor(fontColor);
            positions.add(pos.copy());
            if (tbox.getDimension().getX() > maxW) {
                maxW = tbox.getDimension().getX();
            }
            totalH += tbox.getDimension().getY();
            boxes.add(tbox);
            pos.sum(new Vector(0, tbox.getDimension().getY()));
        }
        setSize(new Vector(maxW, totalH));
    }

    @Override
    public void setPosition(Vector position) {
        this.position = position;
        init();
    }
}
