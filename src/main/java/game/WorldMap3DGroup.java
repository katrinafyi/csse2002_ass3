package game;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;

public class WorldMap3DGroup extends Group {
    private static final int B = 32;

    private final PerspectiveCamera camera = new PerspectiveCamera();

    private final BlockWorldInteraction interaction = new BlockWorldInteraction();

    public WorldMap3DGroup() {

    }

    public BlockWorldInteraction getWorldInteraction() {
        return interaction;
    }


    private void addTestShapes() {
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
        camera.setTranslateX(-90);
        camera.setTranslateY(-90);
        camera.setFarClip(10000.0);


        this.getChildren().add(new AmbientLight(Color.WHITE));

    }

    private Shape3D generateCubeMesh() {
        int w = B;
        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(
                0f, 0f,
                1f, 0f,
                1f, 1f,
                0f, 1f
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
        phong.setDiffuseMap(new Image(getClass().getResourceAsStream("/gold_block.png")));
        //meshView.setMaterial(phong);
        meshView.setMaterial(phong);
        meshView.setTranslateX(0);
        meshView.setTranslateY(0);
        meshView.setTranslateZ(0);

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
