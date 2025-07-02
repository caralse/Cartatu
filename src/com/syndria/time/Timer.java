package com.syndria.time;

public class Timer {
    private final double time;
    private double t;

    private Runnable end;
    private boolean complete;

    public Timer(double time) {
        this.time = time;
        t = 0;
        complete = false;
    }

    public void wait(double dt) {
        t += dt;
        if (t >= time) {
            if (end != null && !complete) {
                end.run();
            }
            complete = true;
        }
    }

    public void atEndTime(Runnable end) {
        this.end = end;
    }


    public double getElapsed() {
        return t;
    }

    public boolean hasStarted() {
        return t > 0;
    }

    public boolean isComplete() {
        return complete;
    }
}
