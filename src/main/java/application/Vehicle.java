package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Vehicle extends Sprite {

    public Vehicle(Layer layer, Vector2D location, Vector2D velocity, Vector2D acceleration, double width, double height) {
        super(layer, location, velocity, acceleration, width, height);
        this.targetIndex= new SimpleIntegerProperty(0);
        this.killCount=new SimpleIntegerProperty(0);
        this.foolCount=new SimpleIntegerProperty(0);
        this.x= new SimpleIntegerProperty((int)location.x);
        this.y= new SimpleIntegerProperty((int)location.y);
    }

    @Override
    public Node createView() {
        return Utils.createArrowImageView( (int) width);
    }

    private SimpleIntegerProperty targetIndex;
    private SimpleIntegerProperty killCount;
    private SimpleIntegerProperty foolCount;


    private SimpleIntegerProperty x;

    private SimpleIntegerProperty y;

    public void updateLocation(){
        x.setValue((int)location.x);
        y.setValue((int)location.y);
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
    public int getTargetIndex() {
        return targetIndex.getValue();
    }

    public SimpleIntegerProperty targetIndexProperty() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex.setValue(targetIndex);
    }

    public void incTargetIndex() {
        this.targetIndex.setValue(this.getTargetIndex()+1);
    }
    public int getKillCount() {
        return killCount.getValue();
    }

    public SimpleIntegerProperty killCountProperty() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount.setValue(killCount);
    }

    public void incKillCount() {
        this.killCount.setValue(this.getKillCount()+1);
    }
    public int getFoolCount() {
        return foolCount.getValue();
    }

    public SimpleIntegerProperty foolCountProperty() {
        return foolCount;
    }

    public void setFoolCount(int foolCount) {
        this.foolCount.setValue(foolCount);
    }
    public void incFoolCount() {
        this.foolCount.setValue(this.getFoolCount()+1);
    }

    public void modifyColor(Color color){

        ((ImageView) this.view).setImage(Utils.createArrowImage((int) width, width/2, color, color.deriveColor(1, 1, 1, 0.3), 1));
    }
}