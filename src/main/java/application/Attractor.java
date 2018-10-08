package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Attractor extends Sprite {

    public Attractor(Layer layer, Vector2D location, Vector2D velocity, Vector2D acceleration, double width, double height) {
        super(layer, location, velocity, acceleration, width, height);
        this.userClickCount=new SimpleIntegerProperty(0);
        this.killCount=new SimpleIntegerProperty(0);
        this.x= new SimpleIntegerProperty((int)location.x);
        this.y= new SimpleIntegerProperty((int)location.y);
    }

    public void updateLocation(){
        x.setValue((int)location.x);
        y.setValue((int)location.y);
    }

    private SimpleIntegerProperty x;

    private SimpleIntegerProperty y;

    private SimpleIntegerProperty userClickCount;

    public int getUserClickCount() {
        return userClickCount.get();
    }

    public SimpleIntegerProperty userClickCountProperty() {
        return userClickCount;
    }

    public void setUserClickCount(int userClickCount) {
        this.userClickCount.set(userClickCount);
    }
    public void incUserClickCount(){
        this.userClickCount.setValue(this.userClickCount.getValue()+1);
    }

    public int getX() {
        return x.getValue();
    }

    public SimpleIntegerProperty xProperty() {
        return x;
    }

    public void setX(int x) {
        this.x.setValue(x);
    }

    public int getY() {
        return y.getValue();
    }

    public SimpleIntegerProperty yProperty() {
        return y;
    }

    public void setY(int y) {
        this.y.setValue(y);
    }

    public SimpleIntegerProperty killCountProperty() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount.set(killCount);
    }

    private SimpleIntegerProperty killCount;

    public void incKillcount(){

        this.killCount.setValue(this.killCount.getValue()+1);
    }

    public int getKillCount(){
        return this.killCount.getValue();
    }

    @Override
    public Node createView() {

        double radius = width / 2;

        Circle circle = new Circle( radius);

        circle.setCenterX(radius);
        circle.setCenterY(radius);

        circle.setStroke(Color.DARKGRAY);
        circle.setFill(Color.DARKGRAY.deriveColor(1, 1, 1, 1));

        return circle;
    }

}