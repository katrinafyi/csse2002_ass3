package game;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;

public class WorldMap3DGroup extends Group {
    private final PerspectiveCamera camera = new PerspectiveCamera();

    public WorldMap3DGroup() {

        Box box = new Box(100, 100, 100);
        box.setTranslateY(0);
        box.setTranslateX(0);
        box.setMaterial(new PhongMaterial(Color.BLACK));
        box.setDrawMode(DrawMode.LINE);
        this.getChildren().add(box);

        camera.setTranslateZ(-500);
    }

    public SubScene generateScene() {
        SubScene scene = new SubScene(this, 500, 500);
        scene.setCamera(camera);
        scene.setFill(Color.MEDIUMAQUAMARINE);
        return scene;
    }
}
