package com.syndria.gfx;

import com.syndria.core.Renderer;
import com.syndria.math.Vector;

import java.awt.*;
import java.awt.image.DataBufferInt;

public class Gfx {

    private static Renderer renderer;
    private static Graphics2D g;

    public Gfx(Renderer r){
        renderer = r;
        g = r.getWindow().getScreen().createGraphics();
    }

    public void drawRect(int x, int y, int w, int h, int c) {
        for (int i = 0; i < w; i++){
            for (int k = 0; k < h; k++) {
                renderer.setPixel(i + x, y + k, c);
            }
        }
    }


    public void drawRect(double dx, double dy, double dw, double dh, int c) {
        int x = (int)dx;
        int y = (int)dy;
        int w = (int)dw;
        int h = (int)dh;
        for (int i = 0; i < w; i++){
            for (int k = 0; k < h; k++) {
                renderer.setPixel(i + x, y + k, c);
            }
        }
    }

    public void drawRect(Vector pos, Vector size, int c) {
        int x = (int)pos.getX();
        int y = (int)pos.getY();
        int w = (int)size.getX();
        int h = (int)size.getY();
        for (int i = 0; i < w; i++){
            for (int k = 0; k < h; k++) {
                renderer.setPixel(i + x, y + k, c);
            }
        }
    }

    public void drawLine(int x0, int y0, int x1, int y1, int color) {
        boolean steep = false;
        if (Math.abs(x0 - x1) < Math.abs(y0 - y1)) {
            int tmp = x0;
            x0 = y0;
            y0 = tmp;
            tmp = x1;
            x1 = y1;
            y1 = tmp;
            steep = true;
        }
        if (x0>x1) {
            int tmp = x0;
            x0 = x1;
            x1 = tmp;
            tmp = y0;
            y0 = y1;
            y1 = tmp;
        }
        int dx = x1 - x0;
        int dy = y1 - y0;
        int derror2 = Math.abs(dy) * 2;
        int error2 = 0;
        int y = y0;
        for (int x = x0; x <= x1; x++) {
            if (steep) {
                renderer.setPixel(y, x, color);
            } else {
                renderer.setPixel(x, y, color);
            }
            error2 += derror2;
            if (error2 > dx) {
                y += (y1 > y0 ? 1 : -1);
                error2 -= dx * 2;
            }
        }
    }

    public void drawimage(Image img, int x, int y) {
        int[] pixels = img.getP();
        for (int i = 0; i < img.getW(); i++){
            for (int k = 0; k < img.getH(); k++) {
                if (((pixels[i + k * img.getW()] >> 24) & 0xFF) > 0) {
                    renderer.setPixel(i + x, y + k, pixels[i + k * img.getW()]);
                }
            }
        }
    }

    public void drawimage(Image img, int x, int y, int c) {
        if (c != -1) {
            int[] pixels = img.getP();
            for (int i = 0; i < img.getW(); i++){
                for (int k = 0; k < img.getH(); k++) {
                    if (((pixels[i + k * img.getW()] >> 24) & 0xFF) > 0) {
                        renderer.setPixel(i + x, y + k, c);
                    }
                }
            }
        } else {
            drawimage(img, x, y);
        }
    }

    public void drawimage(Image img, Vector pos, int c) {
        drawimage(img, (int)pos.getX(), (int) pos.getY(), c);
    }

    public void drawText(Text txt, int x, int y) {
        int[] pixels = ((DataBufferInt)txt.getBufImg().getRaster().getDataBuffer()).getData();
        int w = (int)txt.getDimension().getX();
        int h = (int)txt.getDimension().getY();

        for (int i = 0; i < w; i++){
            for (int k = 0; k < h; k++) {
                if (((pixels[i + k * w] >> 24) & 0xFF) > 0) {
                    renderer.setPixel(i + x, y + k, txt.getColor());
                }
            }
        }
    }

    public void drawText(Text txt, Vector pos) {
        int[] pixels = ((DataBufferInt)txt.getBufImg().getRaster().getDataBuffer()).getData();
        int w = (int)txt.getDimension().getX();
        int h = (int)txt.getDimension().getY();
        int x = (int)pos.getX();
        int y = (int)pos.getY();

        for (int i = 0; i < w; i++){
            for (int k = 0; k < h; k++) {
                if (((pixels[i + k * w] >> 24) & 0xFF) > 0) {
                    renderer.setPixel(i + x, y + k, txt.getColor());
                }
            }
        }
    }

    public Graphics getAWT(){
        return g;
    }

    public Vector getScreenCenter() {
        return new Vector(renderer.getWindow().getScreen().getWidth()/2, renderer.getWindow().getScreen().getHeight()/2);
    }

    public Vector getScreenSize() {
        return new Vector(renderer.getWindow().getScreen().getWidth(), renderer.getWindow().getScreen().getHeight());
    }
}
