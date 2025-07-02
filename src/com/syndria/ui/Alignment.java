package com.syndria.ui;

import com.syndria.math.Vector;

public interface Alignment {

    void align(UIComponent c, FixedContainer fc);

    static Alignment none() {
        return (c, fc) -> {};
    }

    static Alignment straightAlign() {
        return (c, fc) -> {
            c.setPosition(Vector.sum(fc.getContentPlacement(), new Vector(c.getMargin().getLeft(), c.getMargin().getTop())));
            fc.setContentPlacement(Vector.sum(fc.getContentPlacement(), new Vector(c.getSize().getX() + c.getMargin().getHorizontal(), 0)));
        };
    }


    static Alignment leftAlign() {
        return (c, fc) -> {
            c.setPosition(Vector.sum(fc.getContentPlacement(), new Vector(c.getMargin().getLeft(), c.getMargin().getTop())));
            fc.setContentPlacement(Vector.sum(fc.getContentPlacement(), new Vector(0, c.getSize().getY() + c.getMargin().getBottom())));
        };
    }

    static Alignment lineBreakLeft() {
        return (c, fc) -> {
            c.setPosition(Vector.sum(fc.getContentPlacement(), new Vector(c.getMargin().getLeft(), c.getMargin().getTop())));
            if (fc.getChildren().size() % fc.getLineBreak() == 0) {
                fc.setContentPlacement(Vector.sum(new Vector(fc.getPosition().getX(), fc.getContentPlacement().getY()),
                        new Vector(0, c.getSize().getY() + c.getMargin().getBottom())));
            } else {
                fc.setContentPlacement(Vector.sum(fc.getContentPlacement(),
                        new Vector(c.getSize().getX() + c.getMargin().getRight(), 0)));
            }
        };
    }

    static Alignment rightAlign() {
        return (c, fc) -> {
            c.setPosition(Vector.sum(fc.getContentPlacement(), new Vector(fc.getSize().getX() -
                    c.getSize().getX() - c.getMargin().getRight(), c.getMargin().getTop())));
            fc.getContentPlacement().sum(new Vector(0, c.getSize().getY() + c.getMargin().getBottom()));
        };
    }

    static Alignment centerAlign() {
        return (c, fc) -> {
            c.setPosition(Vector.sum(fc.getContentPlacement(), new Vector((fc.getSize().getX() -
                    c.getSize().getX()) / 2, c.getMargin().getTop())));
            fc.setContentPlacement(Vector.sum(new Vector(fc.getPosition().getX(), c.getPosition().getY()), new Vector(0,c.getSize().getY() + c.getMargin().getBottom())));
        };
    }

    static Alignment centerAbsolute() {
        return (c, fc) -> {
            Vector delta = Vector.sub(fc.getSize(), c.getSize());
            delta.scale(0.5d);
            c.setPosition(Vector.sum(fc.getPosition(), delta));
        };
    }

    static Alignment inLine() {
        return (c, fc) -> {
            int widthSum = 0;
            fc.setContentPlacement(fc.getPosition());
            for (UIComponent child : fc.getChildren()) {
                widthSum += (int)child.getSize().getX() + child.getMargin().getLeft() + child.getMargin().getRight();
            }

            int center = ((int)fc.getSize().getX() - widthSum) / 2;
            for (UIComponent child : fc.getChildren()) {
                child.setPosition(Vector.sum(fc.getContentPlacement(), new Vector( center + child.getMargin().getLeft(), child.getMargin().getTop())));
                fc.setContentPlacement(Vector.sum(fc.getContentPlacement(), new Vector(child.getSize().getX() + child.getMargin().getLeft() +
                        child.getMargin().getRight(), 0)));
            }
        };
    }

    static Alignment inLineVertical() {
        return (c, fc) -> {
            int heightSum = 0;
            fc.setContentPlacement(fc.getPosition());
            for (UIComponent child : fc.getChildren()) {
                heightSum += (int)child.getSize().getY() + child.getMargin().getTop() + child.getMargin().getBottom();
            }

            int center = ((int)fc.getSize().getY() - heightSum) / 2;
            for (UIComponent child : fc.getChildren()) {
                child.setPosition(Vector.sum(fc.getContentPlacement(), new Vector((fc.getSize().getX() - child.getSize().getX())/2 ,
                        center + child.getMargin().getTop())));
                fc.setContentPlacement(Vector.sum(fc.getContentPlacement(), new Vector(0,
                        child.getSize().getY() + child.getMargin().getVertical())));
            }
        };
    }
}