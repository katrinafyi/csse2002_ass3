package game;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class WorldMap3DView extends Group {
    private final PerspectiveCamera camera = new PerspectiveCamera();

    public WorldMap3DView() {

        Box box = new Box(100, 100, 100);
        box.setTranslateY(0);
        box.setTranslateX(0);
        box.setMaterial(new PhongMaterial(Color.BLACK));
        this.getChildren().add(box);

        camera.setFieldOfView(100);
    }

    public SubScene generateScene() {
        SubScene scene = new SubScene(this, 500, 500);
        scene.setCamera(camera);
        return scene;
    }
}
