package game.view;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.TooLowException;
import game.model.BlockType;
import game.model.Direction;
import game.model.ReadOnlyBlockWorldModel;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.ErrorEvent;
import game.model.events.MessageEvent;
import game.model.events.WorldMapLoadedEvent;
import game.util.Cache;
import game.util.Utilities;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Main view of the game's world map. Implemented as a square 9x9 grid of
 * {@link Pane} instances, each containing a {@link TileSquare}.
 */
public class GameWorldMapView extends UniformGridPane {
    // We don't ever remove items from this unless a new map is loaded.
    // Potentially, memory usage could increase without bound, but we
    // only generate tiles as they are encountered so this is not much of a
    // problem.
    /** Mapping of position to tile square instances. */
    private final Map<Position, TileSquare> tileSquareMap = new HashMap<>();
    /** Cache of tile heights, used for computing AO. */
    private final Cache<Position, Integer> tileHeights = new Cache<>(this::getTileHeight);
    /** 2-dimensional array of tile panes. Indexed as tilePanes[col][row]. */
    private final Pane[][] tilePanes;

    /** Model to use for getting world map state. */
    private final ReadOnlyBlockWorldModel model;

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
    public GameWorldMapView(ReadOnlyBlockWorldModel model) {
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

        positionLabel = new Label("not displayed");
        applyMessageLabelStyle(positionLabel, "black");
        // Align neatly to top right.
        GridPane.setHalignment(positionLabel, HPos.RIGHT);
        GridPane.setValignment(positionLabel, VPos.TOP);
        positionLabel.setPadding(new Insets(2, 2, 3, 3));
        positionLabel.setVisible(false); // Hide by default.

        model.addListener(WorldMapLoadedEvent.class, this::worldMapLoadedHandler);
        model.addListener(BuilderMovedEvent.class, this::builderMovedHandler);
        model.addListener(BlocksChangedEvent.class, this::blocksChangedHandler);

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

    /**
     * Update exit, height and ambient occlusion state of all tiles.
     */
    private void updateVisibilities() {
        for (TileSquare square : tileSquareMap.values()) {
            square.setExitVisibility(exitsVisible);
            square.setHeightVisibility(heightsVisible);
            square.getAmbientOcclusion().setVisible(ambientOcclusionOn);
        }
    }

    /**
     * Styles message labels with the appropriate styles. Text colour is bold
     * white, and the given colour is applied as the background colour.
     * @param label Label object.
     * @param colour Background colour.
     */
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

    /**
     * Computes and returns the height of the tile at the given position.
     * @param position Position of tile.
     * @return Number of blocks on tile.
     */
    private int getTileHeight(Position position) {
        Tile tile = model.getTile(position);
        if (tile == null) {
            return 0;
        }
        return model.getTile(position).getBlocks().size();
    }

    /**
     * Updates tiles on the changed block and updates the lighting of all
     * its neighbours.
     * @param event Blocks changed event.
     */
    private void blocksChangedHandler(BlocksChangedEvent event) {
        Position position = event.getPosition();
        updateTileBlocks(position);
        updateAOAllNeighbours(position);
    }

    /**
     * Updates the height and top block of the tile at the given position.
     * @param position Position of tile to update.
     */
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

    /**
     * Updates the ambient occlusion of all neighbours (8 blocks) surrounding
     * the given position, as well as the position itself.
     * @param position Position to update.
     */
    private void updateAOAllNeighbours(Position position) {
        updateAOSingle(position);
        Position[] neighbours = getAdjacentPositions(position);
        for (int i = 0; i < 8; i++) {
            updateAOSingle(neighbours[i]);
        }
    }

    /**
     * Updates ambient occlusion of the single tile at the given position.
     * @param position Position of tile to update
     */
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

    /**
     * Gets the 8 positions immediately adjacent to the given position,
     * starting north-west and proceeding clockwise.
     * @param centre Centre position.
     * @return Array of adjacent positions.
     */
    private Position[] getAdjacentPositions(Position centre) {
        Position[] positions = new Position[8];

        // Start at north-west of the given position.
        int curX = centre.getX()-1;
        int curY = centre.getY()-1;

        int dx = 1;
        int dy = 0;
        int oldDy;
        // Gets 8 positions. Starts by moving horizontally to the right.
        for (int i = 0; i < 8; i++) {
            positions[i] = new Position(curX, curY);

            // At corners, rotate 90 degrees clockwise.
            if (i % 2 == 0 && i != 0) {
                oldDy = dy;
                dy = dx;
                dx = -oldDy;
            }

            // Shift to the next position.
            curX += dx;
            curY += dy;
        }
        return positions;
    }

    /**
     * Handles a builder moved event by updating tiles and the position label.
     * @param event Builder moved event.
     */
    @SuppressWarnings("unused")
    private void builderMovedHandler(BuilderMovedEvent event) {
        //clearTilePanes();
        drawTilesToGrid();
        updatePositionLabel();
    }

    /**
     * Clear all tiles from the grid.
     */
    private void clearTilePanes() {
        for (Pane[] paneColumn : tilePanes) {
            for (Pane pane : paneColumn) {
                pane.getChildren().clear();
            }
        }
    }

    /**
     * Clear all internal state of the world map.
     */
    private void resetInternalState() {
        clearTilePanes();
        tileHeights.clear();
        tileSquareMap.clear();
    }

    /**
     * Updates the position label's text with the current position.
     */
    private void updatePositionLabel() {
        positionLabel.setText(model.getCurrentPosition().toString());
    }

    /**
     * Draws all tiles to the grid, including clearing positions which have
     * no tiles.
     */
    private void drawTilesToGrid() {
//        long start = System.nanoTime();
        Position current = model.getCurrentPosition();
        int curX = current.getX();
        int curY = current.getY();

        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                // Position index of the current cell.
                Position pos = new Position(curX+c-halfCols, curY+r-halfRows);
                TileSquare tile = getOrMakeSquare(pos);
                if (tile == null) { // No tile at this position.
                    tilePanes[c][r].getChildren().clear();
                    continue;
                }
                tile.setBuilderTile(pos.equals(current));
                tilePanes[c][r].getChildren().setAll(tile);
            }
        }
//        double time = (System.nanoTime()-start)/1000000.0;
//        System.out.println("Frame time: " + time + " ms ("
//                + (int)(1/time*1000) + " fps)");
    }

    /**
     * Returns the {@link TileSquare} associated for the given position, or
     * null if there is no tile at the position.
     *
     * Creates a new tile square if none has previously been generated.
     * @param pos Position of tile.
     * @return Tile square of position.
     */
    private TileSquare getOrMakeSquare(Position pos) {
        Tile tile = model.getTile(pos);
        if (tile == null) {
            return null;
        }
        TileSquare square = tileSquareMap.get(pos);
        if (square == null) {
            square = new TileSquare();
            // Set width based on the number of columns.
            square.maxWidthProperty().bind(widthProperty().divide(columns));
            Map<String, Tile> exits = tile.getExits();

            // Add all exits.
            for (Direction direction : Direction.values()) {
                square.setHasExit(direction, exits.containsKey(direction.name()));
            }
            // Add to mapping.
            tileSquareMap.put(pos, square);
            // Apply visibilities.
            square.setExitVisibility(exitsVisible);
            square.setHeightVisibility(heightsVisible);
            square.getAmbientOcclusion().setVisible(ambientOcclusionOn);
            // Update visual state.
            updateTileBlocks(pos);
            updateAOSingle(pos);
        }
        return square;
    }

    /**
     * Refreshes the display as appropriate for loading a new world map.
     * @param event World map loaded event.
     */
    @SuppressWarnings("unused")
    private void worldMapLoadedHandler(WorldMapLoadedEvent event) {
        resetInternalState();

        drawTilesToGrid();
        updatePositionLabel();
        positionLabel.setVisible(true);
    }

    /**
     * Shows the fading error message in red, with text from the given event.
     * @param event Error event.
     */
    private void showErrorMessage(ErrorEvent event) {
        errorLabel.showAndFade(event.getMessage());
    }

    /**
     * Shows an info message in green, with text from the given event.
     * @param event Message event.
     */
    private void showNormalMessage(MessageEvent event) {
        successLabel.showAndFade(event.getMessage());
    }
}
