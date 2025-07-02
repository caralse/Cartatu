package com.syndria.math;

public class Vector {
    private double x;
    private double y;

    public Vector (double x, double y){
        this.x = x;
        this.y = y;
    }

    public static Vector sum(Vector u1, Vector u2) {
        return new Vector(u1.getX() + u2.getX(), u1.getY() + u2.getY());
    }

    public static Vector sub(Vector u1, Vector u2) {
        return new Vector(u1.getX() - u2.getX(), u1.getY() - u2 .getY());
    }

    public Vector sub(Vector v) {
        this.x /= v.x;
        this.y /= v.y;
        return this;
    }

    public static Vector mul(Vector v, Vector u) {
        return new Vector(v.getX() * u.getX(), v.getY() * u.getY());
    }

    public static Vector div(Vector v, Vector u) {
        return new Vector(v.getX() / u.getX(), v.getY() / u.getY());
    }

    public Vector sum(Vector v){
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public static Vector addScalar(Vector v, Double d) {
        return new Vector(v.x + d, v.y + d);
    }

    public void addScalar(double s) {
        this.x += s;
        this.y += s;
    }

    public static Vector scale(Vector v, double s) {
        return new Vector(v.x * s, v.y * s);
    }

    public static Vector scale(Vector v, Vector u) {
        return new Vector(v.x * u.x, v.y * u.y);
    }

    public Vector scale(double s) {
        this.x *= s;
        this.y *= s;
        return this;
    }

    public Vector scale(Vector scale) {
        this.x *= scale.x;
        this.y *= scale.y;
        return this;
    }

    public double norm() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public void normalize() {
        this.x /= norm();
        this.y /= norm();
    }

    @Override
    public String toString(){
        return "x: " + this.x + " , y: " + this.y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Vector copy() { return new Vector(this.x, this.y); }

    public boolean equals(Vector v){
        return v.x == this.x && v.y == this.y;
    }

}
