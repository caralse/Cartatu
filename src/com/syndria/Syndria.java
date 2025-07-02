package com.syndria;

import com.syndria.core.GameContainer;

public abstract class Syndria extends GameContainer {

    protected Syndria(String title, int w, int h, float s) {
        super(title, w, h, s);
    }

    protected Syndria() {}

    @Override
    public void load() {}; // Called before starting the game loop

    public abstract void update(double dt);
    public abstract void draw(double alpha);

}
