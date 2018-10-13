package game;

import csse2002.block.world.Block;
import csse2002.block.world.GrassBlock;
import csse2002.block.world.Position;
import csse2002.block.world.SoilBlock;
import csse2002.block.world.StoneBlock;
import csse2002.block.world.Tile;
import csse2002.block.world.WoodBlock;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldMap3DGroup extends Group {

    private static final int BLOCK = 32;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private final RotatingCamera camera = new RotatingCamera();
    private final BlockWorldInteraction interaction;

    private final Map<Tile, List<Shape3D>> tileGroups = new HashMap<>();

    private final Map<Class<? extends Block>, Image> blockTextures = new HashMap<>();

    {
        Map<Class<? extends Block>, Pair<String, String>> textureFiles = new HashMap<>();
        textureFiles.put(GrassBlock.class, new Pair<>("/grass_top2.png", "/grass_block_side.png"));
        textureFiles.put(WoodBlock.class, new Pair<>("/oak_planks.png", null));
        textureFiles.put(StoneBlock.class, new Pair<>("/stone.png", null));
        textureFiles.put(SoilBlock.class, new Pair<>("/dirt.png", null));

        for (Class<? extends Block> type : textureFiles.keySet()) {
            String topTexture = textureFiles.get(type).getKey();
            String sideTexture = textureFiles.get(type).getValue();
            if (sideTexture == null) {
                sideTexture = topTexture;
            }
            blockTextures.put(type,
                    tileImages(loadImage(topTexture), loadImage(sideTexture)));
        }
    }

    public WorldMap3DGroup(BlockWorldInteraction interaction) {
        this.interaction = interaction;

        interaction.addMapCallback(this::worldMapLoadHandler);

        addTestShapes();
        setupCameraAndLight();
    }

    public RotatingCamera getCamera() {
        return camera;
    }

    private void setupCameraAndLight() {
        PointLight light = new PointLight(Color.WHITE);

//        light.translateXProperty().bind(camera.translateXProperty());
//        light.translateYProperty().bind(camera.translateYProperty());
//        light.translateZProperty().bind(camera.translateZProperty().subtract(400));

//        camera.setTranslateZ(-400);
//        camera.setTranslateX(-250);
//        camera.setTranslateY(-100);

        camera.setFieldOfView(100);

        light.setTranslateZ(-4000);
        light.setTranslateX(0);
        light.setTranslateY(0);

        PointLight light2 = new PointLight(Color.WHITE);
        light2.setTranslateZ(-4000);
        light2.setTranslateY(100);
        light2.setTranslateX(100);

        this.getChildren().addAll( light2);

    }

    private void bindTranslate(Node target, Node source) {
        target.translateXProperty().bind(source.translateXProperty());
        target.translateYProperty().bind(source.translateYProperty());
        target.translateZProperty().bind(source.translateZProperty());
    }

    private void addTestShapes() {
        Shape3D b = generateShape(Color.BLACK);
        this.getChildren().add(b);

        Group xGroup = new Group();

        for (int i = 0; i < 9; i++) {
            Shape3D b2 = generateShape(Color.RED);
            b2.setTranslateX((i)* BLOCK);
            xGroup.getChildren().add(b2);

            Shape3D b3 = generateShape(Color.GREEN);
            b3.setTranslateY((i+1)* BLOCK);
            this.getChildren().add(b3);

            Shape3D b4 = generateShape(Color.BLUE);
            b4.setTranslateZ((i+1)* BLOCK);
            this.getChildren().add(b4);

//            Shape3D b5 = generateShape(Color.PURPLE);
//            b5.setTranslateZ((i+1)*-BLOCK);
//            this.getChildren().add(b5);
        }

        xGroup.setTranslateX(2* BLOCK);
        this.getChildren().add(xGroup);


        Shape3D mesh = generateBlock(GrassBlock.class);
        mesh.setTranslateZ(-BLOCK);
        this.getChildren().add(mesh);
    }

    private List<Shape3D> generateBlocksOnTile(Position position, Tile tile) {
        List<Shape3D> children = new ArrayList<>();
        Point2D scenePos = positionToScene(position);

        int i = 0;
        for (Block block : tile.getBlocks()) {
            Shape3D blockShape = generateBlock(block.getClass());
            blockShape.setTranslateZ(i*-BLOCK);
            blockShape.setTranslateX(scenePos.getX());
            blockShape.setTranslateY(scenePos.getY());
            children.add(blockShape);
            i++;
        }
        return children;
    }

    private Shape3D generateBlock(Class<? extends Block> blockClass) {
        return generateCubeMesh(blockTextures.get(blockClass));
    }

    private Shape3D generateCubeMesh(Image diffuseMap) {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(
                // Side texture is bottom half.
                0f, 0.5f,
                0f, 1f,
                1f, 1f,
                1f, 0.5f,

                // Top textures are top half.
                0f, 0f,
                0f, 0.5f,
                1f, 0.5f,
                1f, 0f
        );

        // Half width. Used to centre the cube.
        float hw = BLOCK / 2f;
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

                // Bottom face. Uses same textures as top.
                6,4, 2,5, 7,7,
                7,7, 2,5, 3,6
        );

        MeshView meshView = new MeshView(mesh);

        PhongMaterial phong = new PhongMaterial();
        phong.setDiffuseMap(diffuseMap);

        meshView.setMaterial(phong);
        meshView.setTranslateX(0);
        meshView.setTranslateY(0);
        meshView.setTranslateZ(0);

        return meshView;
    }

    private Shape3D generatePlayerCube() {
        double playerSize = BLOCK /2f;
        Box box = new Box(playerSize, playerSize, playerSize);
        box.setMaterial(new PhongMaterial(Color.CORAL));
        return box;
    }

    private Image loadImage(String resourcePath) {
        return new Image(getClass().getResourceAsStream(resourcePath));
    }

    private Image tileImages(Image image1, Image image2) {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(new ImageView(image1), new ImageView(image2));
        Image snap = vBox.snapshot(new SnapshotParameters(), null);
        /*try {
            ImageIO.write(SwingFXUtils.fromFXImage(snap, null), "png", new File(image1.toString()+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return snap;
    }

    private Shape3D generateShape(Color color) {
        //noinspection SuspiciousNameCombination
        Box box = new Box(BLOCK, BLOCK, BLOCK);
        box.setTranslateY(0);
        box.setTranslateX(0);
        box.setTranslateZ(0);
        PhongMaterial phong = new PhongMaterial(color);
        box.setMaterial(phong);
        return box;
    }

    public SubScene generateScene() {
        SubScene scene = new SubScene(this, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        scene.setFill(Color.MEDIUMAQUAMARINE);
        return scene;
    }

    private Point2D positionToScene(Position pos) {
        Position start = interaction.getWorldMap().getStartPosition();
        return new Point2D(pos.getX() - start.getX(), pos.getY() - start.getY())
                .multiply(BLOCK);
    }

    private void removeAllTiles() {
        for (List<Shape3D> shapes : tileGroups.values()) {
            this.getChildren().removeAll(shapes);
        }
        tileGroups.clear();
    }

    private void worldMapLoadHandler() {
        System.out.println("handling loaded in 3d map");
        removeAllTiles();

        for (Position position : interaction.getPositionTileMap().keySet()) {
            Tile tile = interaction.getPositionTileMap().get(position);
            List<Shape3D> tileGroup = generateBlocksOnTile(position, tile);
            tileGroups.put(tile, tileGroup);
            this.getChildren().addAll(tileGroup);
        }

        this.getChildren().add(generatePlayerModel());
    }

    private Shape3D generatePlayerModel() {
        Shape3D player = generatePlayerCube();

        Position start = interaction.getWorldMap().getStartPosition();
        Tile tile = interaction.getWorldMap().getTile(start);

        bindCameraToPlayer(player);

        player.setTranslateZ(-BLOCK * (1+tile.getBlocks().size()));
        player.setTranslateX(0);
        player.setTranslateY(0);


        return player;
    }

    private void bindCameraToPlayer(Shape3D player) {
        camera.getTranslation().xProperty().bind(player.translateXProperty().subtract(WIDTH/2f));
        camera.getTranslation().yProperty().bind(player.translateYProperty().subtract(HEIGHT/2f));
        camera.getTranslation().setZ(-400);
    }
}
