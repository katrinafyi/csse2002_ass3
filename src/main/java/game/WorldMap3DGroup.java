package game;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;

import java.util.AbstractMap;

public class WorldMap3DGroup extends Group {
    private static final int B = 32;

    private final PerspectiveCamera camera = new PerspectiveCamera();

    public WorldMap3DGroup() {

        Shape3D b = generateShape(Color.WHITE);
        this.getChildren().add(b);

        for (int i = 0; i < 4; i++) {
            Shape3D b2 = generateShape(Color.BLUE);
            b2.setTranslateX((i+1)*B);
            this.getChildren().add(b2);

            Shape3D b3 = generateCubeMesh();
            b3.setTranslateY((i+1)*B);
            this.getChildren().add(b3);
        }


        camera.setTranslateZ(-50);

    }

    private Shape3D generateCubeMesh() {
        int w = B;
        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(
                0, 0,
                1, 0,
                1, 1,
                0, 1
        );

        mesh.getPoints().addAll(
                0, 0, 0,
                w, 0, 0,
                w, w, 0,
                0, w, 0,
                0, 0, w,
                0, w, 0,
                w, w, w,
                0, w, w
        );

        mesh.getTexCoords().addAll(
                // Side faces.
                4,0, 5,1, 0,3,
                5,1, 1,2, 0,3,

                5,6, 6,1, 1,3,
                6,1, 2,2, 1,3,

                6,0, 7,1, 2,3,
                7,1, 3,2, 2,3,

                7,0, 4,1, 3,3,
                4,1, 0,2, 3,3,

                // Bottom face.
                0,0, 1,1, 3,3,
                1,1, 2,2, 3,3,

                // Top face.
                7,0, 6,1, 4,3,
                6,1, 5,2, 4,3
        );

        MeshView meshView = new MeshView(mesh);

        PhongMaterial phong = new PhongMaterial();
        phong.setDiffuseMap(new Image("file:src/main/resources/grass_block_side.png"));
        meshView.setMaterial(phong);
        meshView.setCullFace(CullFace.NONE);
        meshView.setTranslateX(0);
        meshView.setTranslateY(0);
        meshView.setTranslateZ(0);

        meshView.setMaterial(new PhongMaterial(Color.YELLOW));
        meshView.setDrawMode(DrawMode.FILL);

        return meshView;
    }

    private Shape3D generateShape(Color color) {
        Box box = new Box(B, B, B);
        box.setTranslateY(0);
        box.setTranslateX(0);
        box.setTranslateZ(0);
        PhongMaterial phong = new PhongMaterial();
        phong.setDiffuseMap(new Image("file:src/main/resources/dirt.png"));
        box.setMaterial(phong);
        return box;
    }

    public SubScene generateScene() {
        SubScene scene = new SubScene(this, 500, 500, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        scene.setFill(Color.MEDIUMAQUAMARINE);
        return scene;
    }
}
