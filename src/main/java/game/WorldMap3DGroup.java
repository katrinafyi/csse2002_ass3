package game;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;

public class WorldMap3DGroup extends Group {
    private final PerspectiveCamera camera = new PerspectiveCamera();

    public WorldMap3DGroup() {

        Shape3D b = generateShape(Color.WHITE);
        this.getChildren().add(b);

        for (int i = 0; i < 4; i++) {
            Shape3D b2 = generateShape(Color.BLUE);
            b2.setTranslateX((i+1)*100);
            this.getChildren().add(b2);

            Shape3D b3 = generateShape(Color.RED);
            b3.setTranslateY((i+1)*100);
            this.getChildren().add(b3);
        }


        camera.setTranslateZ(-900);

    }

    private Shape3D generateShape(Color color) {
        Box box = new Box(100, 100, 100);
        box.setTranslateY(0);
        box.setTranslateX(0);
        box.setTranslateZ(0);
        box.setMaterial(new PhongMaterial(color));
        box.setDrawMode(DrawMode.LINE);
        return box;
    }

    public SubScene generateScene() {
        SubScene scene = new SubScene(this, 500, 500);
        scene.setCamera(camera);
        scene.setFill(Color.MEDIUMAQUAMARINE);
        return scene;
    }


}
