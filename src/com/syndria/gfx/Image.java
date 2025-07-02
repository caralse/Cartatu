package com.syndria.gfx;

import com.syndria.Syndria;
import com.syndria.math.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Image {
    private int w, h;
    private int [] p;
    private BufferedImage img = null;
    private String path;

    public Image(String path) {

        this.path = path;

        try (InputStream is = Image.class.getResourceAsStream(path) ){
            img = ImageIO.read(is);
            w = img.getWidth();
            h = img.getHeight();
            p = img.getRGB(0, 0, w, h, null, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }

        img.flush();
    }

    public Image(BufferedImage img) {
        this.img = img;
        w = img.getWidth();
        h = img.getHeight();
        p = img.getRGB(0, 0, w, h, null, 0, w);

        img.flush();
    }

    private Image(Image img, int x, int y, int w, int h) {
        this.img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        this.w = w;
        this.h = h;
        int []p = img.getImg().getRGB(x, y, w, h, null, 0, w);
        this.img.setRGB(0, 0, w, h, p, 0, w);
        this.img.flush();
    }

    public static Image getQuad(Image img, int x, int y, int width, int height) {
        return new Image(img, x, y, width, height);
    }

    public void scale(double w, double h) {
        int newWidth = (int)(img.getWidth() * w);
        int newHeight = (int)(img.getHeight() * h);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, img.getWidth(),
                img.getHeight(), null);
        g.dispose();
        img = resized;
        this.w = resized.getWidth();
        this.h = resized.getHeight();
        this.p = resized.getRGB(0, 0, newWidth, newHeight, null, 0, newWidth);
    }

    public void scale(Vector scale) {
        scale(scale.getX(), scale.getY());
    }

    public static Image scale(Image img, Vector scale) {
        Image scaled = new Image(img, 0, 0, img.w, img.h);
        scaled.scale(scale);
        return scaled;
    }

    public void draw(int x, int y) {
        Syndria.gfx.getAWT().drawImage(this.img, x, y, null);
    }

    public void draw(double x, double y) {
        Syndria.gfx.getAWT().drawImage(this.img, (int)x, (int)y, null);
    }

    public void draw(Vector pos) {
        Syndria.gfx.getAWT().drawImage(this.img, (int)pos.getX(), (int)pos.getY(), null);
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public Vector getSize() {
        return new Vector(w, h);
    }

    public void setH(int h) {
        this.h = h;
    }

    public int[] getP() {
        return p;
    }

    public void setP(int[] p) {
        this.p = p;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void free() {
        this.img = null;
        System.gc();
    }
}
