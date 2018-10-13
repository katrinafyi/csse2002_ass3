package game;

import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;

public class RotatingCamera extends PerspectiveCamera {
    private double pivotX;
    private double pivotY;
    private double pivotZ;

    private final Rotate hRotate = new Rotate();

    {
        hRotate.setAxis(new Point3D(0, 0, 1));
        this.getTransforms().addAll(hRotate);
    }

    public void setPivot(double x, double y, double z) {
        this.pivotX = x;
        this.pivotY = y;
        this.pivotZ = z;
        hRotate.setPivotX(x);
        hRotate.setPivotY(y);
        hRotate.setPivotZ(z);
    }


    private Rotate getHorizontalRotate() {
        return hRotate;
    }



}
