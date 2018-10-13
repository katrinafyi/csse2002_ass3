package game;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.awt.*;

public class RotatingCamera extends PerspectiveCamera {
    private double pivotX;
    private double pivotY;
    private double pivotZ;

    private final Rotate hRotate = new Rotate();
    private final Rotate pitchRotate = new Rotate();
    private final Translate translation = new Translate();

    {
        hRotate.setAxis(Rotate.Z_AXIS);
        pitchRotate.setAxis(new Point3D(1, 0, 0));
        
        this.getTransforms().addAll(
                hRotate, pitchRotate, translation);
    }

    public void bindRotationPivots(Shape3D pivot) {
        hRotate.pivotXProperty().bind(pivot.translateXProperty());
        hRotate.pivotYProperty().bind(pivot.translateYProperty());
        hRotate.pivotZProperty().bind(pivot.translateZProperty());

        pitchRotate.pivotXProperty().bind(pivot.translateXProperty());
        pitchRotate.pivotYProperty().bind(pivot.translateYProperty());
        pitchRotate.pivotZProperty().bind(pivot.translateZProperty());
    }

    public void rotateHorizontal(double angle) {
        hRotate.setAngle(hRotate.getAngle() + angle);
    }

    public void rotateVertical(double angle) {
        pitchRotate.setAngle(pitchRotate.getAngle() + angle);
    }

    public Translate getTranslation() {
        return translation;
    }

    public Rotate getHRotate() {
        return hRotate;
    }

    public Rotate getVRotate() {
        return pitchRotate;
    }
}
