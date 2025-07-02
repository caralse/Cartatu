package com.syndria.core;

import com.syndria.math.Vector;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Window w;

    private final int NUM_KEYS = 256;
    private final int[] keyState = new int[NUM_KEYS];
    private final boolean[] keyDown = new boolean[NUM_KEYS];

    private final int NUM_BUTTONS= 5;
    private final boolean[] buttonDown = new boolean[NUM_BUTTONS];
    private final int[] buttonState = new int[NUM_BUTTONS];

    private int mouseX, mouseY;
    private int scroll;

    private final List<Event> events = new ArrayList<>();

    private final ArrayList<KeyEvent> typedKeys = new ArrayList<>();

    private class Event {
        private final int type;
        private final int value;
        public Event(int type, int value){
            this.type = type;
            this.value = value;
        }
        private void dispatch(){
            switch (this.type) {

                case 0 -> { //KEYPRESSED
                    keyState[this.value] = 1;
                    keyDown[this.value] = true;
                }
                case 1 -> { //KEYRELEASED
                    keyState[this.value] = 2;
                    keyDown[this.value] = false;
                }
                case 2 -> { //MOUSEPRESSED
                    buttonState[this.value] = 1;
                    buttonDown[this.value] = true;
                }
                case 3 -> { //MOUSERELEASED
                    buttonState[this.value] = 2;
                    buttonDown[this.value] = false;
                }
                case 4 -> { //MOUSEMOVED
                    mouseX = (int)((this.value >> 16) / w.getScale());
                    mouseY = (int)((this.value & 0x0000FFFF) / w.getScale());
                }
                case 5 -> { //MOUSEDRAGGED
                    mouseX = (int)((this.value >> 16) / w.getScale());
                    mouseY = (int)((this.value & 0x0000FFFF) / w.getScale());
                }
                case 6 -> { //MOUSEWHEEL
                    scroll = this.value;
                }
                default -> {
                    scroll = 0;
                }
            }
        }
    }

    public Input(Window wn) {
        w = wn;
        mouseX = 0;
        mouseY = 0;
        scroll = 0;

        clear();

        w.getCanvas().addKeyListener(this);
        w.getCanvas().addMouseListener(this);
        w.getCanvas().addMouseMotionListener(this);
        w.getCanvas().addMouseWheelListener(this);
    }

    public void pullEvent(){
        try {
            while (!events.isEmpty()) {
                Event e = events.removeFirst();
                if (e != null) e.dispatch();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        scroll = 0;
    }

    public boolean isKeyDown(int keyCode) {
        return keyDown[keyCode];
    }

    public boolean KeyReleased(int keyCode) {
        if (keyState[keyCode] == 2 && !keyDown[keyCode]){
            keyState[keyCode] = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean KeyPressed(int keyCode) {
        if (keyState[keyCode] == 1 && keyDown[keyCode]){
            keyState[keyCode] = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean isMouseDown(int bCode) {
        return buttonDown[bCode];
    }

    public boolean MouseReleased(int bCode) {
        if (buttonState[bCode] == 2 && !buttonDown[bCode]){
            buttonState[bCode] = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean MousePressed(int bCode) {
        if (buttonState[bCode] == 1 && buttonDown[bCode]){
            buttonState[bCode] = 0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        events.add(new Event(6, e.getWheelRotation()));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        events.add(new Event(5, (e.getX() << 16) | e.getY()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        events.add(new Event(4, (e.getX() << 16) | e.getY()));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!buttonDown[e.getButton()]){
            events.add(new Event(2, e.getButton()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        events.add(new Event(3, e.getButton()));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        typedKeys.add(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!keyDown[e.getKeyCode()]){
            events.add(new Event(0, e.getKeyCode()));
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        events.add(new Event(1, e.getKeyCode()));
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public Vector getMousePosition() { return new Vector(mouseX, mouseY); }

    public int getMouseWheel() {
        return scroll;
    }

    public KeyEvent getTypedKey() {
        try {
            return typedKeys.removeFirst();
        } catch (Exception _) {
            return null;
        }
    }

    public void clear() {
        Arrays.fill(keyState, 0);
        Arrays.fill(keyDown, false);
        Arrays.fill(buttonState, 0);
        Arrays.fill(buttonDown, false);
    }
}
