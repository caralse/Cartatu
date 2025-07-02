package com.syndria.ui;

import com.syndria.Syndria;
import com.syndria.gfx.Text;
import com.syndria.math.Vector;

import java.awt.event.KeyEvent;

public class TextInput extends UIComponent {

    private String t;
    private Text text;
    private int fontSize;
    private int bgColor;
    private boolean isPassword;
    private int maxLength;
    private int txtColor;

    public TextInput(int fontSize, Vector size, int bgColor, int txtColor, boolean isPassword) {
        t = "";
        this.fontSize = fontSize;
        text = new Text(" ", fontSize, txtColor);
        this.size = size;
        this.bgColor = bgColor;
        this.txtColor = txtColor;
        text.setColor(txtColor);
        this.isPassword = isPassword;
        maxLength = 10;
    }

    public TextInput(int fontSize, Vector size, int bgColor) {
        this(fontSize, size, bgColor, 0xFFFFF1E8, false);
    }

    @Override
    public void draw(double alpha) {
        Syndria.gfx.drawRect(position, size, bgColor);
        Syndria.gfx.drawText(text, position);
    }

    @Override
    public void update(double dt) {
        KeyEvent c = Syndria.input.getTypedKey();
        if (c != null) {
            if (c.getKeyChar() == '\b') {
                try {
                    t = t.substring(0, t.length() - 1);
                } catch (Exception _) {
                    t = "";
                }
            } else if (c.getKeyChar() == KeyEvent.CHAR_UNDEFINED || !isAlphanumeric(c.getKeyChar())){

            } else {
                if (t.length() < maxLength) {
                    t += c.getKeyChar();
                }
            }
            text = new Text(t, fontSize, txtColor);
        }
    }

    private boolean isAlphanumeric(final int codePoint) { // Actually includes . and /
        return (codePoint >= 65 && codePoint <= 90) ||
                (codePoint >= 97 && codePoint <= 122) ||
                (codePoint >= 46 && codePoint <= 57); // 46 is .
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getText() {
        return t;
    }
}
