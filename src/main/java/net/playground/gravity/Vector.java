package net.playground.gravity;

public class Vector {
    private double XValue;
    private double YValue;

    // constructor
    public Vector(double value, double angle) {
        this.XValue = value * Math.cos(angle * Math.PI / 180);
        this.YValue = -1 * value * Math.sin(angle * Math.PI / 180);
    }

    // getters and setters
    public double getXValue() {
        return XValue;
    }

    public double getYValue() {
        return YValue;
    }

}
