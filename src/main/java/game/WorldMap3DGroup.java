package game;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
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
        Shape3D b = generateShape(Color.BLACK);
        this.getChildren().add(b);

        for (int i = 0; i < 4; i++) {
            Shape3D b2 = generateShape(Color.RED);
            b2.setTranslateX((i+1)*B);
            this.getChildren().add(b2);

            Shape3D b3 = generateShape(Color.GREEN);
            b3.setTranslateY((i+1)*B);
            this.getChildren().add(b3);

            Shape3D b4 = generateShape(Color.BLUE);
            b4.setTranslateZ((i+1)*B);
            this.getChildren().add(b4);
        }


        Shape3D mesh = generateCubeMesh();
        mesh.setTranslateZ(-B);
        this.getChildren().add(mesh);

        camera.setTranslateZ(-50);
        camera.setTranslateX(-90);
        camera.setTranslateY(-90);
        camera.setFarClip(10000.0);

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
                // Side textures are left half of image.
                0f, 0f,
                0f, 1f,
                0.5f, 1f,
                0.5f, 0f,

                // Top textures are right half.
                0.5f, 0f,
                0.5f, 1f,
                1f, 1f,
                1f, 0f
        );

        // Half width. Used to centre the cube.
        float hw = B/2f;
        mesh.getPoints().addAll(
                // Bottom 4 corners.
                -hw, hw, -hw,
                hw, hw, -hw,
                hw, hw, hw,
                -hw, hw, hw,
                // Top 4 corners.
                -hw, -hw, -hw,
                hw, -hw, -hw,
                hw, -hw, hw,
                -hw, -hw, hw
        );

        mesh.getFaces().addAll(
                // Top face (xy plane).
                4,4, 0,5, 5,7,
                5,7, 0,5, 1,6,

                // Side faces.
                0,0, 3,1, 1,3,
                1,3, 3,1, 2,2,

                1,0, 2,1, 5,3,
                5,3, 2,1, 6,2,

                4,0, 5,1, 6,3,
                6,3, 5,1, 7,2,

                4,0, 7,1, 0,3,
                0,3, 7,1, 3,2,

                // Bottom face.
                6,0, 2,1, 7,3,
                7,3, 2,1, 3,2
        );

        MeshView meshView = new MeshView(mesh);

        PhongMaterial phong = new PhongMaterial();
        phong.setDiffuseMap(
tileImages(                new Image(getClass().getResourceAsStream("/tnt_side.png")),
        new Image(getClass().getResourceAsStream("/tnt_top.png"))));
        //meshView.setMaterial(phong);
        meshView.setMaterial(phong);
        meshView.setTranslateX(0);
        meshView.setTranslateY(0);
        meshView.setTranslateZ(0);

        return meshView;
    }

    private Image tileImages(Image image1, Image image2) {
        HBox hbox = new HBox();
        hbox.getChildren().addAll(new ImageView(image1), new ImageView(image2));
        return hbox.snapshot(new SnapshotParameters(), null);
    }

    private Shape3D generateShape(Color color) {
        Box box = new Box(B, B, B);
        box.setTranslateY(0);
        box.setTranslateX(0);
        box.setTranslateZ(0);
        PhongMaterial phong = new PhongMaterial(color);
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
