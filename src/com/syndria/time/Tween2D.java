package com.syndria.time;

import com.syndria.math.Vector;

public class Tween2D {
    private Vector startV;
    private Vector endV;
    private double duration;
    private boolean isComplete;
    private double elapsedTime = 0d;
    private Vector vector;

    public Tween2D(Vector startV, Vector endV, double duration) {
        this.startV = startV;
        this.endV = endV;
        this.duration = duration;
        this.isComplete = false;
        vector = new Vector(0 ,0);
    }

    public Vector update(double dt) {
        if (isComplete) return endV;

        elapsedTime += dt;

        if (elapsedTime >= duration) {
            isComplete = true;
            return endV;
        }

        // Linear interpolation
        double t = elapsedTime / duration;
        vector = Vector.sum(Vector.scale(startV, 1 - t), Vector.scale(endV, t));
        return vector;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean hasStarted() {
        return elapsedTime > 0;
    }

    public void setEndVector(Vector endV) {
        this.endV = endV;
    }

    public Vector getVector() {
       return isComplete ? endV : vector;
    }

    public void reset() {
        isComplete = false;
        vector = new Vector(0, 0);
        elapsedTime = 0d;
    }
}

