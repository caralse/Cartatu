package com.syndria.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateManager {
    private State currentState;
    private final HashMap<String, State> states;
    private final List<State> stack;

    public StateManager(){
        this.states = new HashMap<>(1);
        this.stack = new ArrayList<>();
    }

    public void update(double dt){
        try {
            for (State s : stack) {
                s.update(dt);
                if (s.blocksWhenPushed()) {
                    return;
                }
            }
        } catch (Exception _){}
        currentState.update(dt);
    }

    public void draw(double alpha){
        currentState.draw(alpha);

        try {
            for (State s : stack) {
                s.draw(alpha);
            }
        } catch (Exception _) {}
    }

    public void add(State s) {
        states.put(s.getLabel(), s);
    }

    public void remove(String label) {
        this.states.remove(label);
    }

    public void switchTo(String label) {
        currentState = states.get(label);
        currentState.enter();
    }

    public void push(String label) {
        stack.addFirst(states.get(label));
        states.get("label").enter();
    }

    public void push(State unnamed) {
        stack.addFirst(unnamed);
        unnamed.enter();
    }

    public void pop() {
        stack.removeFirst();
    }

    public void clearStack() {
        stack.clear();
    }

    public void clear() {
        clearStack();
        this.currentState = null;
        this.states.clear();
    }

    public void setCurrentState(String state) {
        currentState = states.get(state);
    }

}
