package com.syndria.state;

public abstract class State {
    protected String label;

    public State(String label) {
        this.label = label;
    }

    public State(){};

    public abstract void enter();
    public abstract void draw(double alpha);
    public abstract void update(double dt);
    public abstract boolean blocksWhenPushed();

    public String getLabel() { return this.label; };

}
