package com.syndria.core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Window {
    private static int WIDTH = 320, HEIGHT = 240;
    private static float SCALE = 3f;
    private static String title;
    private final JFrame frame;
    private final Canvas canvas;
    private final BufferedImage screen;
    private final BufferStrategy bs;
    private final Graphics2D g;

    public Window(String t, int W, int H, float S){
        title = t;
        this.frame = new JFrame(title);
        this.canvas = new Canvas();
        SCALE = S;
        WIDTH = W;
        HEIGHT = H;
        this.canvas.setSize((int)(W*SCALE), (int)(H*SCALE));

        this.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());

        this.frame.setVisible(true);
        this.frame.add(canvas, BorderLayout.CENTER);
        this.frame.setResizable(false);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.canvas.requestFocus();

        this.screen = new BufferedImage(WIDTH, HEIGHT, TYPE_INT_RGB);

        this.canvas.createBufferStrategy(2);

        this.bs = canvas.getBufferStrategy();
        this.g = (Graphics2D) bs.getDrawGraphics();
    }

    public Window(){
        title = "Syndria Engine Beta";
        this.frame = new JFrame(title);
        this.canvas = new Canvas();
        this.canvas.setSize((int)(WIDTH*SCALE), (int)(HEIGHT*SCALE));

        this.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());

        this.frame.setResizable(false);
        this.frame.setVisible(true);
        this.frame.add(canvas, BorderLayout.CENTER);

        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.canvas.requestFocus();

        this.screen = new BufferedImage(WIDTH, HEIGHT, TYPE_INT_RGB);

        this.canvas.createBufferStrategy(2);

        this.bs = canvas.getBufferStrategy();
        this.g = (Graphics2D) bs.getDrawGraphics();
    }

    protected void update(){
        this.g.drawImage(this.screen, 0, 0, this.canvas.getWidth(), this.canvas.getHeight(), null);
        this.bs.show();
    }

    public Canvas getCanvas(){
        return this.canvas;
    }

    public BufferedImage getScreen(){
        return this.screen;
    }

    public Graphics getGraphics(){
        return this.g;
    }

    public float getScale(){
        return SCALE;
    }

}
