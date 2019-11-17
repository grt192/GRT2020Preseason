import java.lang.Math;

public class Position {

    private double x;
    private double y;
    // Keeps track of the theta value used to determine the position
    private double theta;

    public Position() {
        x = 0;
        y = 0;
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Position(double distance, double theta, double x, double y) {
        this.x = distance * Math.cos(theta) + x;
        this.y = distance * Math.sin(theta) + y;
        this.theta = theta;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getTheta() {
        return theta;
    }

    public String toString() {
        return "X: " + x + " Y: " + y + " theta: " + theta;
    }

    public double distance(Position a) {
        return Math.sqrt(Math.pow(x - a.getX(), 2) + Math.pow(y - a.getY(), 2));
    }

    public void addVector(double x, double y) {
        this.x += x;
        this.y += y;
    }

}