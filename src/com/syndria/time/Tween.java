package com.syndria.time;

public class Tween {
    private final double startValue;
    private final double endValue;
    private final double duration;
    private boolean isComplete;
    private double elapsedTime = 0d;

    public Tween(double startValue, double endValue, double duration) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.duration = duration;
        this.isComplete = false;
    }

    public double update(double dt) {
        if (isComplete) return endValue;

        elapsedTime += dt;

        if (elapsedTime >= duration) {
            isComplete = true;
            return endValue;
        }

        // Linear interpolation
        double t = elapsedTime / duration;
        return (startValue < endValue) ? (1 - t) * startValue + t * endValue : (1 - t) * startValue - t * endValue;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
