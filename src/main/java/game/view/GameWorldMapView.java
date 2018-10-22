package game.view;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.TooLowException;
import game.util.Cache;
import game.util.Utilities;
import game.model.BlockType;
import game.model.BlockWorldModel;
import game.model.Direction;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.ErrorEvent;
import game.model.events.MessageEvent;
import game.model.events.WorldMapLoadedEvent;
import game.view.components.FadingLabel;
import game.view.components.TileSquare;
import game.view.components.UniformGridPane;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameWorldMapView extends UniformGridPane {
    private final Cache<Position, Integer> tileHeights = new Cache<>(this::getTileHeight);

    private final Map<Position, Image> tileCache = new HashMap<>();
    private final TileSquare renderTile;
    private final Scene renderScene;
    private final Canvas mainCanvas;
    private final ImageView[][] imageViews;

    private final BlockWorldModel model;

    private final FadingLabel successLabel;
    private final FadingLabel errorLabel;
    private final StackPane successPane;
    private final StackPane errorPane;

    private boolean exitsVisible = false;
    private boolean heightsVisible = false;
    private boolean ambientOcclusionOn = true;

    private final static Color BACKGROUND_COLOR = Color.SKYBLUE;

    public GameWorldMapView(BlockWorldModel model) {
        super(9, 9, 0);
        this.model = model;
        imageViews = new ImageView[columns][rows];

        renderTile = new TileSquare();
        renderScene = new Scene(renderTile);
        renderTile.maxWidthProperty().bind(heightProperty().divide(rows/2));

        mainCanvas = new Canvas();
        mainCanvas.widthProperty().bind(this.widthProperty());
        mainCanvas.heightProperty().bind(this.heightProperty());
        mainCanvas.getGraphicsContext2D().fillRect(100, 0, 100, 100);
        add(mainCanvas, 0, 0, columns, rows);

        this.setPrefWidth(495.0); // Multiple of 9.

        errorLabel = new FadingLabel(Duration.seconds(1), Duration.millis(500));
        setMessageLabelStyle(errorLabel, "#911414");
        successLabel = new FadingLabel(Duration.seconds(1), Duration.millis(500));
        setMessageLabelStyle(successLabel, "#167708");

        errorPane = new StackPane();
        errorPane.getChildren().add(errorLabel);
        errorPane.prefWidthProperty().bind(widthProperty());
        errorPane.prefHeightProperty().bind(heightProperty().divide(rows));

        successPane = new StackPane();
        successPane.getChildren().add(successLabel);
        successPane.prefWidthProperty().bind(widthProperty());
        successPane.prefHeightProperty().bind(heightProperty().divide(rows));

        model.addListener(WorldMapLoadedEvent.class, this::worldMapLoadedHandler);
        model.addListener(BuilderMovedEvent.class, this::builderMovedHandler);
        model.addListener(BlocksChangedEvent.class, this::blocksChangedHandler);
        model.addListener(null, this::allHandler);

        model.addListener(ErrorEvent.class, this::showErrorMessage);
        model.addListener(MessageEvent.class, this::showNormalMessage);

        Utilities.usePrefWidthHeight(this);

        TileSquare head = new TileSquare();
        head.setBuilderTile(true);
        head.maxWidthProperty().bind(widthProperty().divide(columns));
        add(head, halfCols, halfRows);

        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                ImageView view = new ImageView();
                view.setPreserveRatio(true);
                view.fitWidthProperty().bind(heightProperty().divide(rows));
                imageViews[c][r] = null;
                add(view, c, r);
            }
        }
        drawMessageLabels();
    }

    private void drawMessageLabels() {
        add(errorPane, 0, 3, columns, 1);
        add(successPane, 0, 3, columns, 1);
    }

    public boolean isExitsVisible() {
        return exitsVisible;
    }

    public void setExitsVisible(boolean exitsVisible) {
        this.exitsVisible = exitsVisible;
        updateVisibilities();
    }

    public boolean isHeightsVisible() {
        return heightsVisible;
    }

    public void setHeightsVisible(boolean heightsVisible) {
        this.heightsVisible = heightsVisible;
        updateVisibilities();
    }

    public boolean isAmbientOcclusionOn() {
        return ambientOcclusionOn;
    }

    public void setAmbientOcclusionOn(boolean ambientOcclusionOn) {
        this.ambientOcclusionOn = ambientOcclusionOn;
        updateVisibilities();
    }

    @SuppressWarnings("unused")
    private void resizeChildren(ObservableValue<? extends Number> obs,
                                Number oldValue, Number newValue) {
        if (model.getCurrentPosition() != null) {
            redrawTiles();
        }
    }

    private void updateVisibilities() {
        tileCache.clear();
        redrawTiles();
    }

    private static void setMessageLabelStyle(FadingLabel label, String colour) {
        label.setPadding(new Insets(10));
        label.setStyle(
                "-fx-font-size: 15;"
                + "-fx-font-weight: bold;"
                +"-fx-text-fill: white;"
                + "-fx-background-color: "+colour+";"
                + "-fx-border-radius: 5;"
        );
        GridPane.setHalignment(label, HPos.CENTER);
    }

    private int getTileHeight(Position position) {
        Tile tile = model.getTile(position);
        if (tile == null) {
            return 0;
        }
        return model.getTile(position).getBlocks().size();
    }

    private void allHandler(BaseBlockWorldEvent event) {
        System.out.println("View caught: " + event);
    }

    private void blocksChangedHandler(BlocksChangedEvent event) {
        Position position = event.getPosition();

        tileHeights.remove(position);
        updateTileBlocks(position);
        updateAOAllNeighbours(position);
        redrawTiles();
    }

    private void updateTileBlocks(Position position) {
        tileCache.remove(position);
    }

    private void updateAOAllNeighbours(Position position) {
//        updateAOSingle(position);
        tileCache.remove(position);
        Position[] neighbours = getAdjacentPositions(position);
        for (int i = 0; i < 8; i++) {
            tileCache.remove(neighbours[i]);
            continue;
//            updateAOSingle(neighbours[i]);
        }
        System.out.println(Arrays.toString(getAdjacentPositions(model.getCurrentPosition())));
    }

    private void updateAOSingle(TileSquare tileSquare, Position position) {
        if (tileSquare == null) {
            return; // No tile exists here. Nothing to do.
        }

        int thisHeight = tileHeights.get(position);
        Position[] adjPositions = getAdjacentPositions(position);
        int[] adjacent = new int[8];

        for (int i = 0; i < 8; i++) {
            adjacent[i] = tileHeights.get(adjPositions[i]) - thisHeight;
        }

        tileSquare.getAmbientOcclusion().setAdjacent(adjacent);
    }

    private Position[] getAdjacentPositions(Position centre) {
        Position[] positions = new Position[8];

        int curX = centre.getX()-1;
        int curY = centre.getY()-1;

        int dx = 1;
        int dy = 0;
        int oldDy;
        for (int i = 0; i < 8; i++) {
            positions[i] = new Position(curX, curY);

            if (i % 2 == 0 && i != 0) {
                oldDy = dy;
                dy = dx;
                dx = -oldDy;
            }

            curX += dx;
            curY += dy;
        }
        return positions;
    }

    @SuppressWarnings("unused")
    private void builderMovedHandler(BuilderMovedEvent event) {
        redrawTiles();
    }

    private void clearTilePanes() {
        for (ImageView[] col : imageViews) {
            for (ImageView view : col) {
            }
        }
    }

    private void resetInternalState() {
        clearTilePanes();
        tileHeights.clear();
        tileCache.clear();
    }

    private void redrawTiles() {
        long startTime = System.nanoTime();
        Position current = model.getCurrentPosition();
        int curX = current.getX();
        int curY = current.getY();

        GraphicsContext graphics = mainCanvas.getGraphicsContext2D();

        double cellWidth = getWidth()/columns;
        double cellHeight = getHeight()/rows;

        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                // Position index of the current cell.
                Position pos = new Position(curX+ c -halfCols, curY+ r -halfRows);
                Image image = getOrRenderTileImage(pos);
                if (image != null) {
                    graphics.drawImage(image, c * cellWidth, r * cellHeight,
                            cellWidth, cellHeight);
                } else {
                    graphics.clearRect(c*cellWidth, r*cellHeight, cellWidth, cellHeight);
                }

            }
        }
        System.out.println("Frame time: " + (System.nanoTime()-startTime)/1000000.0 + "ms");
    }

    private Image getOrRenderTileImage(Position pos) {
        Tile tile = model.getTile(pos);
        if (tile == null) {
            return null;
        }
        Image image = tileCache.get(pos);
        if (image == null) {
            image = renderOneTile(pos);
            tileCache.put(pos, image);
        }
        return image;
    }

    private Image renderOneTile(Position position) {
        Tile tile = model.getTile(position);
        int height = tile.getBlocks().size();
        Map<String, Tile> exits = tile.getExits();

        BlockType topBlock;
        try {
            topBlock = height == 0 ? null
                    : BlockType.fromBlock(tile.getTopBlock());
        } catch (TooLowException e) {
            throw new AssertionError(e);
        }

        renderTile.setTopBlock(topBlock);
        renderTile.setHeight(height);
        tileHeights.put(position, height);

        for (Direction direction : Direction.values()) {
            renderTile.setHasExit(direction, exits.containsKey(direction.name()));
        }

        renderTile.setExitVisibility(exitsVisible);
        renderTile.setHeightVisibility(heightsVisible);
        renderTile.getAmbientOcclusion().setVisible(ambientOcclusionOn);

        updateAOSingle(renderTile, position);

        return renderTile.snapshot(new SnapshotParameters(), null);
    }

    @SuppressWarnings("unused")
    private void worldMapLoadedHandler(WorldMapLoadedEvent event) {
        resetInternalState();
        System.out.println("map loaded v2");
        redrawTiles();
    }

    private TileSquare newTileSquare() {
        TileSquare tile = new TileSquare();
        tile.maxWidthProperty().bind(widthProperty().divide(columns));
        return tile;
    }

    private void showErrorMessage(ErrorEvent event) {
        errorLabel.showAndFade(event.getMessage());
    }

    private void showNormalMessage(MessageEvent event) {
        successLabel.showAndFade(event.getMessage());
    }
}
