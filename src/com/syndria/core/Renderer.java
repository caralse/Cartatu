package com.syndria.core;

import com.syndria.math.Vector;

import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Renderer {
    private final Window w;
    private int[] pixels;
    private final int[] bg;
    private final int RES, W, H;
    //ADD NORMAL MAPS

    public Renderer(Window w){
        this.w = w;
        pixels = ((DataBufferInt)this.w.getScreen().getRaster().getDataBuffer()).getData();
        RES = pixels.length;
        W = w.getScreen().getWidth();
        H = w.getScreen().getHeight();
        bg = new int[RES];
        Arrays.fill(bg, 0xFF000000);
    }

    protected void update() {
        w.update();
    }

    public void setPixel(int x, int y, int c) {
        if(x >= W || y >= H || x < 0 || y < 0) {
            return;
        }
        int color = c;

        int alpha = (color >> 24) & 0xFF;

        if ( alpha < 255 ){
            color = alphaBlend(pixels[y * W + x], c, alpha/255d);
        }
        this.pixels[y * W + x] = color;
    }

    public void setPixel(Vector pos, int c) {
        int x = (int)pos.getX();
        int y = (int)pos.getY();

        if(x >= W || y >= H || x < 0 || y < 0) {
            return;
        }
        int color = c;

        int alpha = (color >> 24) & 0xFF;

        if ( alpha < 255 ){
            color = alphaBlend(pixels[y * W + x], c, alpha/255d);
        }
        this.pixels[y * W + x] = color;
    }

    private int alphaBlend(int base, int c, double alpha) {
        int baseR = getRed(base);
        int baseG = getGreen(base);
        int baseB = getBlue(base);

        int newRed = (int)((1-alpha) * baseR + alpha * getRed(c));
        int newGreen = (int)((1-alpha) * baseG + alpha * getGreen(c));
        int newBlue = (int)((1-alpha) * baseB + alpha * getBlue(c));

        return (newRed << 16 | newGreen << 8 | newBlue);
    }

    private int linearRGB(int c) {
        double sRGB = c / 255d;
        return sRGB <= 0.04045 ? (int)(sRGB / 12.92 * 255) : (int)((Math.pow((sRGB + 0.055) / 1.055, 2.4))*255);
    }

    private int sRGB(int c) {
        double linearRGB = c / 255d;
        return linearRGB <= 0.0031308 ? (int)(linearRGB * 12.92 * 255) : (int)((1.055 * Math.pow(linearRGB, 1 / 2.4) - 0.055) * 255);
    }

    private int getRed(int c) {
        return (c >> 16) & 0xFF;
    }

    private int getGreen(int c) {
        return (c >> 8) & 0xFF;
    }

    private int getBlue(int c) {
        return c & 0xFF;
    }

    public void clear(){
        System.arraycopy(this.bg, 0, this.pixels, 0, this.RES);
    }

    public Window getWindow() {
        return w;
    }
}
