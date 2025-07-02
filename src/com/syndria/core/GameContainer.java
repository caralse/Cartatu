package com.syndria.core;

import com.syndria.gfx.Gfx;
import com.syndria.gfx.Text;

public abstract class GameContainer implements Runnable {
    private final Thread gameThread;
    private final Window window;
    public static Renderer renderer;
    public static Input input;
    public static Gfx gfx;
    private static final double UPS = 1f / 30f;
    private static int FPS = 0;
    private int fps = 0;

    public static SyndriaSplashState SyndriaSplashState = new SyndriaSplashState("SyndriaSplashState");

    public GameContainer(){
        this.window = new Window();
        renderer = new Renderer(this.window);
        input = new Input(this.window);
        gfx = new Gfx(renderer);
        this.gameThread = new Thread(this);
    }

    public GameContainer(String title, int W, int H, float S){
        this.window = new Window(title, W, H, S);
        renderer = new Renderer(this.window);
        input = new Input(this.window);
        gfx = new Gfx(renderer);
        this.gameThread = new Thread(this);
    }

    public void start() {
        gameThread.start();
    }

    public void load(){};
    public abstract void update(double dt);
    public abstract void draw(double alpha);

    public void run() {
        double dt = 0f;
        double seconds = 0f;
        double lag = 0f;
        double oldTime = System.nanoTime();
        double currentTime = 0f;

        Text.loadFonts();
        load();

        while(gameThread.isAlive()){
            currentTime = System.nanoTime();
            dt = (currentTime - oldTime) / 1_000_000_000f;
            oldTime = currentTime;
            fps++;
            lag += dt;
            seconds += dt;

            input.pullEvent();

            while (lag >= UPS) {
                update(UPS);
                lag -= UPS;
                input.clear();
            }
            process(lag / UPS);

            if (seconds >= 1f) {
                FPS = fps;
                fps = 0;
                seconds = 0f;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void process(double alpha) {
        renderer.clear();
        draw(alpha);
        renderer.update();
    }

    public static int getFPS() {
        return FPS;
    }
}
