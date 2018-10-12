package game;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;

import java.util.HashMap;
import java.util.Map;

public class WorldMap3DGroup extends Group {
    private static final int B = 32;
    private final PerspectiveCamera camera = new PerspectiveCamera();
    private final BlockWorldInteraction interaction = new BlockWorldInteraction();

    private final Map<Position, Tile> positionTileMap = new HashMap<>();

    public WorldMap3DGroup() {
        addTestShapes();
    }

    private void resetMapState() {

    }

    private void drawTile(Position position, Tile tile) {

    }

    public BlockWorldInteraction getWorldInteraction() {
        return interaction;
    }


    private void addTestShapes() {
        Shape3D b = generateShape(Color.WHITE);
        this.getChildren().add(b);

        for (int i = 0; i < 4; i++) {
            Shape3D b2 = generateCubeMesh();
            b2.setTranslateX((i+1)*B);
            this.getChildren().add(b2);

            Shape3D b3 = generateTestMesh();
            b3.setTranslateY((i+1)*B);
            this.getChildren().add(b3);
        }


        camera.setTranslateZ(-50);
        camera.setTranslateX(90);
        camera.setTranslateY(-90);
        camera.setFarClip(10000.0);

        camera.setRotationAxis(new Point3D(1, 0, 0));
        camera.setRotate(90);

        camera.setRotationAxis(new Point3D(0, 0, 1));
        camera.setRotate(90);


        this.getChildren().add(new AmbientLight(Color.WHITE));

    }

    private Shape3D generateTestMesh() {
        TriangleMesh pyramidMesh = new TriangleMesh();
        pyramidMesh.getTexCoords().addAll(0,0);
        float h = B;                    // Height
        float s = B*2;                    // Side
        pyramidMesh.getPoints().addAll(
                0,    0,    0,            // Point 0 - Top
                0,    h,    -s/2,         // Point 1 - Front
                -s/2, h,    0,            // Point 2 - Left
                s/2,  h,    0,            // Point 3 - Back
                0,    h,    s/2           // Point 4 - Right
        );
        pyramidMesh.getFaces().addAll(
                0,0,  2,0,  1,0,          // Front left face
                0,0,  1,0,  3,0,          // Front right face
                0,0,  3,0,  4,0,          // Back right face
                0,0,  4,0,  2,0,          // Back left face
                4,0,  1,0,  2,0,          // Bottom rear face
                4,0,  3,0,  1,0           // Bottom front face
        );
        MeshView pyramid = new MeshView(pyramidMesh);
        pyramid.setDrawMode(DrawMode.FILL);
        pyramid.setMaterial(new PhongMaterial(Color.BLUE));
        return pyramid;
    }

    private Shape3D generateCubeMesh() {
        float w = B;
        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(
                0f, 0f,
                1f, 1f,
                1f, 0f,
                0f, 1f
        );


        mesh.getPoints().addAll(
                0, -0, 0,
                w, -0, 0,
                w, -0, w,
                0, -0, w,
                0, -w, 0,
                0, -0, w,
                w, -w, w,
                0, w, w
        );

        mesh.getFaces().addAll(
                // Side faces.
                4,0, 5,1, 0,3,
                5,1, 1,2, 0,3,

                5,0, 6,1, 1,3,
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
        phong.setDiffuseMap(new Image(getClass().getResourceAsStream("/tnt_side.png")));
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

    public void updateWorldMap(WorldMap worldMap) {
        this.interaction.setWorldMap(worldMap);
    }
}
