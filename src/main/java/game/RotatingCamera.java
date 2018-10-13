package game;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class RotatingCamera extends PerspectiveCamera {
    private double pivotX;
    private double pivotY;
    private double pivotZ;

    private final Rotate hRotate = new Rotate();
    private final Rotate pitchRotate = new Rotate();
    private final Translate translation = new Translate();


    {
        hRotate.setAxis(new Point3D(0, 0, 1));
        pitchRotate.setAxis(new Point3D(1, 0, 0));
        this.getTransforms().addAll(
                hRotate,
                pitchRotate,
                translation);
    }

    public Rotate getHorizontalRotate() {
        return hRotate;
    }

    public void rotateHorizontal(double angle) {
        hRotate.setAngle(hRotate.getAngle() + angle);
    }

    public void rotateVertical(double angle) {
        pitchRotate.setAngle(pitchRotate.getAngle() + angle);
    }

    public Rotate getPitchRotate() {
        return pitchRotate;
    }

    public Translate getTranslation() {
        return translation;
    }
}
