package net.playground.gravity;

import java.util.ArrayList;
import java.util.List;

public class Planet {
    // G: the gravitational constant (6.674×10ˆ−11 N · (m/kg)ˆ2);
    private final double G = 6.674e-11;
    private ArrayList<Point> positionList = new ArrayList<>();

    // planetary properties
    private int id;
    private String name;
    private String color;
    private Point position;
    private double radius;
    private double mass;
    private boolean debugEnabled;
    private double vX;
    private double vY;
    private double totalAccX;
    private double totalAccY;

    // create utility variables for reuse, instead of creating them over and over
    private double dX;
    private double dY;
    private double distance;
    private double acceleration;
    private double aX;
    private double aY;

    private int counter = 0;

    // constructor
    public Planet() {
    }

    // methods
    public void setInitialPosition(Vector initialPosition) {
        this.position = new Point(initialPosition.getXValue(), initialPosition.getYValue());
    }

    public void setInitialVelocity(Vector initialVelocity) {
        this.vX = initialVelocity.getXValue();
        this.vY = initialVelocity.getYValue();
    }

    // calculate gravitational influences from other planets
    public void processGravity(List<Planet> planetList) {
        long start = System.nanoTime();
        this.counter++;
        this.totalAccX = 0;
        this.totalAccY = 0;
        planetList.forEach(p -> calcOneBodyEffect(p));
        this.vX += this.totalAccX;
        this.vY += this.totalAccY;
        if (counter == 500) {
            positionList.add(new Point(this.position.x, this.position.y));
            if (positionList.size() > 1000) {
                positionList.remove(0);
            }
            counter = 0;
        }
        this.position.x += this.vX;
        this.position.y += this.vY;
        long finish = System.nanoTime();
        if (this.debugEnabled) {
            System.out.println(finish - start);
        }
    }

    private void calcOneBodyEffect(Planet planet) {
        if (this.id != planet.getId()) {
            dX = planet.getPosition().getX() - this.position.getX();
            dY = planet.getPosition().getY() - this.position.getY();
            distance = Math.sqrt((dX * dX) + (dY * dY));
            acceleration = G * planet.getMass() / (distance * distance);
            aX = acceleration * dX / distance;
            aY = acceleration * dY / distance;
            this.totalAccX += aX;
            this.totalAccY += aY;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(1024);
        sb.append("Planet: ").append(this.name).append(" => ");
        sb.append("position: (").append(this.position.getX()).append(", ").append(this.position.getY()).append("), ");
        sb.append("velocity: (").append(this.vX).append(", ").append(this.vY).append(").");
        return sb.toString();
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public double getvX() {
        return vX;
    }

    public double getvY() {
        return vY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<Point> getPositionList() {
        return positionList;
    }

}
