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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Main view of the game's world map. Implemented as a square 9x9 grid of
 * {@link Pane} instances, each containing a {@link TileSquare}.
 */
public class GameWorldMapView extends UniformGridPane {
    /** Mapping of position to tile square instances. */
    private final Map<Position, TileSquare> tileSquareMap = new HashMap<>();
    /** Cache of tile heights, used for computing AO. */
    private final Cache<Position, Integer> tileHeights = new Cache<>(this::getTileHeight);
    /** 2-dimensional array of tile panes. Indexed as tilePanes[col][row]. */
    private final Pane[][] tilePanes;

    /** Model to use for getting world map state. */
    private final BlockWorldModel model;

    /** Label for showing success messages. Coloured green. */
    private final FadingLabel successLabel;
    /** Label for showing error messages. Coloured red. */
    private final FadingLabel errorLabel;
    /** Pane containing and aligning the success label. */
    private final StackPane successPane;
    /** Pane containing and aligning the error label. */
    private final StackPane errorPane;

    private final Label positionLabel;

    /** Whether tile exits are currently visible. */
    private boolean exitsVisible = false;
    /** Whether tile heights are currently visible. */
    private boolean heightsVisible = false;
    /** Whether ambient occlusion is currently enabled. */
    private boolean ambientOcclusionOn = true;

    /**
     * Constructs a new world map view from the given model.
     * @param model Game model.
     */
    public GameWorldMapView(BlockWorldModel model) {
        super(9, 9, 0);
        this.model = model;
        tilePanes = new Pane[columns][rows];

        Utilities.setBackground(this, Color.SKYBLUE);

        errorLabel = new FadingLabel(Duration.seconds(1), Duration.millis(500));
        errorLabel.setWrapText(true);
        applyMessageLabelStyle(errorLabel, "#911414");
        successLabel = new FadingLabel(Duration.seconds(1), Duration.millis(500));
        successLabel.setWrapText(true);
        applyMessageLabelStyle(successLabel, "#167708");

        errorPane = new StackPane();
        errorPane.getChildren().add(errorLabel);
        errorPane.prefWidthProperty().bind(widthProperty());

        successPane = new StackPane();
        successPane.getChildren().add(successLabel);
        successPane.prefWidthProperty().bind(widthProperty());

        positionLabel = new Label("–");
        applyMessageLabelStyle(positionLabel, "black");
        // Align neatly to top right.
        GridPane.setHalignment(positionLabel, HPos.RIGHT);
        GridPane.setValignment(positionLabel, VPos.TOP);
        positionLabel.setPadding(new Insets(2, 2, 3, 3));
        positionLabel.setVisible(false); // Hide by default.

        model.addListener(WorldMapLoadedEvent.class, this::worldMapLoadedHandler);
        model.addListener(BuilderMovedEvent.class, this::builderMovedHandler);
        model.addListener(BlocksChangedEvent.class, this::blocksChangedHandler);
        model.addListener(null, this::allHandler);

        model.addListener(ErrorEvent.class, this::showErrorMessage);
        model.addListener(MessageEvent.class, this::showNormalMessage);

        Utilities.usePrefWidthHeight(this);

        // Generate panes in each cell for holding the tile squares.
        // Faster than adding tiles to grid directly as the panes fix the
        // size of the grid when altering tiles.
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                Pane p = new Pane();
                tilePanes[c][r] = p;
                Utilities.usePrefWidthHeight(p);
                p.prefWidthProperty().bind(heightProperty().divide(rows));
                p.prefHeightProperty().bind(heightProperty().divide(rows));
                add(p, c, r);
            }
        }

        // Span all columns to allow for long messages.
        add(errorPane, 0, 2, columns, 3);
        add(successPane, 0, 2, columns, 3);
        add(positionLabel, 0, 0, columns, 1);
    }

    /**
     * Returns whether exits are currently visible on tiles.
     * @return Boolean.
     */
    public boolean isExitsVisible() {
        return exitsVisible;
    }

    /**
     * Sets the visibility of exits.
     * @param exitsVisible Whether exits are visible.
     */
    public void setExitsVisible(boolean exitsVisible) {
        this.exitsVisible = exitsVisible;
        updateVisibilities();
    }

    /**
     * Returns whether heights are currently visible on tiles.
     * @return Boolean.
     */
    public boolean isHeightsVisible() {
        return heightsVisible;
    }

    /**
     * Sets the visibility of heights.
     * @param heightsVisible Whether heights are visible.
     */
    public void setHeightsVisible(boolean heightsVisible) {
        this.heightsVisible = heightsVisible;
        updateVisibilities();
    }

    /**
     * Returns whether ambient occlusion is currently enabled.
     * @return Boolean.
     */
    public boolean isAmbientOcclusionOn() {
        return ambientOcclusionOn;
    }

    /**
     * Sets ambient occlusion.
     * @param ambientOcclusionOn Whether ambient occlusion is enabled.
     */
    public void setAmbientOcclusionOn(boolean ambientOcclusionOn) {
        this.ambientOcclusionOn = ambientOcclusionOn;
        updateVisibilities();
    }

    private void updateVisibilities() {
        for (TileSquare square : tileSquareMap.values()) {
            square.setExitVisibility(exitsVisible);
            square.setHeightVisibility(heightsVisible);
            square.getAmbientOcclusion().setVisible(ambientOcclusionOn);
        }
    }

    private static void applyMessageLabelStyle(Label label, String colour) {
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
        updateTileBlocks(position);
        updateAOAllNeighbours(position);
    }

    private void updateTileBlocks(Position position) {
        Tile tile = model.getTile(position);
        TileSquare tileSquare = tileSquareMap.get(position);
        assert tileSquare != null;

        int height = tile.getBlocks().size();
        tileSquare.setHeight(height);
        tileHeights.put(position, height);

        try {
            tileSquare.setTopBlock(height == 0
                    ? null : BlockType.fromBlock(tile.getTopBlock()));
        } catch (TooLowException e) {
            throw new AssertionError(e);
        }
    }

    private void updateAOAllNeighbours(Position position) {
        updateAOSingle(position);
        Position[] neighbours = getAdjacentPositions(position);
        for (int i = 0; i < 8; i++) {
            updateAOSingle(neighbours[i]);
        }
        System.out.println(Arrays.toString(getAdjacentPositions(model.getCurrentPosition())));
    }

    private void updateAOSingle(Position position) {
        TileSquare tileSquare = tileSquareMap.get(position);
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
        //clearTilePanes();
        drawTilesToGrid();
        updatePositionLabel();
    }

    private void clearTilePanes() {
        for (Pane[] paneColumn : tilePanes) {
            for (Pane pane : paneColumn) {
                pane.getChildren().clear();
            }
        }
    }

    private void resetInternalState() {
        clearTilePanes();
        tileHeights.clear();
        tileSquareMap.clear();
    }

    private void updatePositionLabel() {
        positionLabel.setText(model.getCurrentPosition().toString());
    }

    private void drawTilesToGrid() {
        long start = System.nanoTime();
        Position current = model.getCurrentPosition();
        int curX = current.getX();
        int curY = current.getY();

        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                // Position index of the current cell.
                Position pos = new Position(curX+ c -halfCols, curY+ r -halfRows);
                TileSquare tile = getOrMakeSquare(pos);
                if (tile == null) { // No tile at this position.
                    tilePanes[c][r].getChildren().clear();
                    continue;
                }
                tile.setBuilderTile(r == halfRows && c == halfCols);
                tilePanes[c][r].getChildren().setAll(tile);
            }
        }
        double time = (System.nanoTime()-start)/1000000.0;
        System.out.println("Frame time: " + time + " ms ("
                + (int)(1/time*1000) + " fps)");
    }

    private TileSquare getOrMakeSquare(Position pos) {
        Tile tile = model.getTile(pos);
        if (tile == null) {
            return null;
        }
        TileSquare square = tileSquareMap.get(pos);
        if (square == null) {
            square = new TileSquare();
            square.maxWidthProperty().bind(widthProperty().divide(columns));
            Map<String, Tile> exits = tile.getExits();

            for (Direction direction : Direction.values()) {
                square.setHasExit(direction, exits.containsKey(direction.name()));
            }

            tileSquareMap.put(pos, square);

            square.setExitVisibility(exitsVisible);
            square.setHeightVisibility(heightsVisible);
            square.getAmbientOcclusion().setVisible(ambientOcclusionOn);

            updateTileBlocks(pos); // Updates height and top block.
            updateAOSingle(pos);
        }
        return square;
    }

    @SuppressWarnings("unused")
    private void worldMapLoadedHandler(WorldMapLoadedEvent event) {
        resetInternalState();
        System.out.println("map loaded v2");

        drawTilesToGrid();
        updatePositionLabel();
        positionLabel.setVisible(true);
    }

    private void showErrorMessage(ErrorEvent event) {
        errorLabel.showAndFade(event.getMessage());
    }

    private void showNormalMessage(MessageEvent event) {
        successLabel.showAndFade(event.getMessage());
    }
}
