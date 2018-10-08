package application;

import javafx.scene.Node;
import javafx.scene.layout.Region;

public abstract class Sprite extends Region {

    Vector2D location;
    Vector2D velocity;
    Vector2D acceleration;

    double maxForce = Settings.SPRITE_MAX_FORCE;
    double maxSpeed = Settings.SPRITE_MAX_SPEED;

    Node view;

    // view dimensions
    double width;
    double height;
    double centerX;
    double centerY;
    double radius;

    double angle;

    Layer layer = null;

    public Sprite( Layer layer, Vector2D location, Vector2D velocity, Vector2D acceleration, double width, double height) {

        this.layer = layer;

        this.location = location;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.width = width;
        this.height = height;
        this.centerX = width / 2;
        this.centerY = height / 2;

        this.view = createView();

        setPrefSize(width, height);

        // add view to this node
        getChildren().add( view);

        // add this node to layer
        layer.getChildren().add( this);

    }

    public abstract Node createView();

    public void applyForce(Vector2D force) {
        acceleration.add(force);
    }

    public void move() {

        // set velocity depending on acceleration
        this.velocity.add(acceleration);

        // limit velocity to max speed
        // set velocity depending on acceleration
        this.velocity.limit(maxSpeed);

        // change location depending on velocity
        // set velocity depending on acceleration
        this.location.add(velocity);

        // angle: towards velocity (ie target)
        // set velocity depending on acceleration
        this.angle = velocity.heading2D();

        // clear acceleration
        // set velocity depending on acceleration
        this.acceleration.multiply(0);
    }

    /**
     * Move sprite towards target
     */
    public void seek(Vector2D target) {

        Vector2D desired = Vector2D.subtract(target, location);

        // The distance is the magnitude of the vector pointing from location to target.

        double d = desired.magnitude();
      //  desired.normalize();
      //   System.out.println(this.getLocation().x+" "+this.getLocation().y+" "+d);
        // If we are closer than 100 pixels...
        if (d < Settings.SPRITE_SLOW_DOWN_DISTANCE) {

            // ...set the magnitude according to how close we are.
            double m = Utils.map(d, 0, Settings.SPRITE_SLOW_DOWN_DISTANCE, 0, maxSpeed);
            desired.multiply(m);

        }
        // Otherwise, proceed at maximum speed.
        else {
            desired.multiply(maxSpeed);
        }

        // The usual steering = desired - velocity
        Vector2D steer = Vector2D.subtract(desired, velocity);
        steer.limit(maxForce);

        applyForce(steer);

    }

    /**
     * Update node position
     */
    public void display() {

        relocate(location.x - centerX, location.y - centerY);

        setRotate(Math.toDegrees( angle));

    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public Vector2D getLocation() {
        return location;
    }

    public void setLocation( double x, double y) {
        location.x = x;
        location.y = y;
    }

    public void setLocationOffset( double x, double y) {
        location.x += x;
        location.y += y;
    }

}