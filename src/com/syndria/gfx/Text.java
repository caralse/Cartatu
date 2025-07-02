package com.syndria.gfx;

import com.syndria.Syndria;
import com.syndria.math.Vector;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Text {
    private String text;
    private int fontSize;
    private int fontStyle;
    private String fontFamily;
    private int color;

    private final Font font;
    private BufferedImage img;

    private Vector size;

    private static final GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final java.util.List<String> AVAILABLE_FONT_FAMILY_NAMES = Arrays.asList(GE.getAvailableFontFamilyNames());



    public Text(String txt, int fontSize, int color, String fontFamily, int fontStyle) {
        this.text = txt;
        this.color = color;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily.isEmpty() ? "Joystix Monospace" : fontFamily;
        this.font = new Font(this.fontFamily, this.fontStyle, this.fontSize);
        createDrawable();
    }

    public Text(String txt, int fontSize, int color) {
        this(txt, fontSize, color, "Joystix Monospace", Font.PLAIN);
    }

    public Text (int value, int fontSize, int color) {
        this(String.format("%d", value), fontSize, color);
    }

    public void setText(String text) {
        this.text = text;
        createDrawable();
    }

    private void createDrawable(){
        GraphicsConfiguration graphicsConfiguration = GE.getDefaultScreenDevice().getDefaultConfiguration();
        FontMetrics FM = new FontMetrics(font) {
            @Override
            public LineMetrics getLineMetrics(String str, Graphics context) {
                return super.getLineMetrics(str, context);
            }
        };
        int w = FM.getStringBounds(text, Syndria.gfx.getAWT()).getBounds().width;
        int h = FM.getStringBounds(text, Syndria.gfx.getAWT()).getBounds().height;
        this.img = graphicsConfiguration.createCompatibleImage(2+w, h, 2);
        Graphics2D g2d = this.img.createGraphics();
        g2d.setFont(this.font);
        g2d.setColor(Color.WHITE);
        g2d.drawString(this.text, 0, this.fontSize);
        g2d.dispose();
        size = new Vector(img.getWidth(), img.getHeight());
    }

    public static void loadFonts(){
        try {
            final List<InputStream> LIST = Arrays.asList(
                    Text.class.getResourceAsStream("/fonts/joystix.otf"),
                    Text.class.getResourceAsStream("/fonts/visitor.ttf"), // 10px
                    Text.class.getResourceAsStream("/fonts/pixellari.ttf") // 16px
            );
            for (InputStream LIST_ITEM : LIST) {
                if (LIST_ITEM != null) {
                    Font FONT = Font.createFont(Font.TRUETYPE_FONT, LIST_ITEM);
                    if (!AVAILABLE_FONT_FAMILY_NAMES.contains(FONT.getFontName())) {
                        GE.registerFont(FONT);
                    }
                }
            }
        } catch (Exception _) {}
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        createDrawable();
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        createDrawable();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Image getImage() {
        return new Image(img);
    }

    public BufferedImage getBufImg() {
        return img;
    }

    public Vector getDimension() {
        return size;
    }
}
