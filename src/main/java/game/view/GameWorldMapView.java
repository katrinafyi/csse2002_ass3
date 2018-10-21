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
import game.view.components.FastGridPane;
import game.view.components.TileSquare;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameWorldMapView extends FastGridPane {
    private final Map<Direction, int[]> oppositeShifts = new EnumMap<>(Direction.class);


    private final Map<Position, TileSquare> tileSquareMap = new HashMap<>();
    private final Cache<Position, Integer> tileHeights = new Cache<>(this::getTileHeight);
    private final BlockWorldModel model;

    private final FadingLabel successLabel;
    private final FadingLabel errorLabel;
    private final StackPane successPane;
    private final StackPane errorPane;

    private boolean exitsVisible = false;
    private boolean heightsVisible = false;

    public GameWorldMapView(BlockWorldModel model) {
        super(9, 9);
        this.model = model;

        setOppositeShifts();

        // o.O
        Utilities.setBackground(this, Color.SKYBLUE);
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

        this.setMinHeight(Control.USE_PREF_SIZE);
        this.setMaxHeight(Control.USE_PREF_SIZE);
        this.setMinWidth(Control.USE_PREF_SIZE);
        this.setMaxWidth(Control.USE_PREF_SIZE);

        this.heightProperty().addListener(this::resizeChildren);

        drawMessageLabels();
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

    private void resizeChildren(ObservableValue<? extends Number> obs,
                                Number oldValue, Number newValue) {
        if (model.getCurrentPosition() != null) {
            drawTilesToGrid();
        }
    }

    private void setOppositeShifts() {
        int[][] allShifts = new int[][] {
                {-1,  1,  0,  1, 1, 0},
                {-1, -1, -1,  0, 0, 1},
                {-1, -1,  0, -1, 1, 0},
                { 1, -1,  1,  0, 0, 1}
        };
        oppositeShifts.put(Direction.north, allShifts[0]);
        oppositeShifts.put(Direction.east, allShifts[1]);
        oppositeShifts.put(Direction.south, allShifts[2]);
        oppositeShifts.put(Direction.west, allShifts[3]);
    }

    private TileSquare[] getTilesToRemove(Direction movedDirection) {
        int[] shifts = oppositeShifts.get(movedDirection);
        int colShift = (columns-1)/2;
        int rowShift = (rows-1)/2;
        int curX = model.getCurrentPosition().getX() + shifts[0]*colShift + shifts[2];
        int curY = model.getCurrentPosition().getY() + shifts[1]*rowShift + shifts[3];

        TileSquare[] tilesToRemove = new TileSquare[columns];
        for (int i = 0; i < columns; i++) {
            tilesToRemove[i] = tileSquareMap.get(
                    new Position(curX + i*shifts[4], curY + i*shifts[5]));
        }
        return tilesToRemove;
    }

    private void updateVisibilities() {
        for (TileSquare square : tileSquareMap.values()) {
            square.setExitVisibility(exitsVisible);
            square.setHeightVisibility(heightsVisible);
        }
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

        updateTileBlocks(position);
        updateAOAllNeighbours(position);
    }

    private void updateTileBlocks(Position position) {
        Tile tile = model.getTile(position);
        TileSquare tileSquare = tileSquareMap.get(position);
        assert tileSquare != null;

        int height = tile.getBlocks().size();
        tileSquare.setHeight(height);

        try {
            tileSquare.setTopBlock(height == 0
                    ? null : BlockType.fromBlock(tile.getTopBlock()));
        } catch (TooLowException e) {
            throw new AssertionError(e);
        }
        tileHeights.put(position, height);
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



    private void builderMovedHandler(BuilderMovedEvent event) {
        getChildren().removeAll(getTilesToRemove(event.getDirection()));
        drawTilesToGrid();
    }

    private void resetInternalState() {
        this.getChildren().clear();
        tileHeights.clear();
        tileSquareMap.clear();
    }

    private void drawTilesToGrid() {
        Position current = model.getCurrentPosition();
        int curX = current.getX();
        int curY = current.getY();

        int halfCols = (this.columns-1)/2;
        int halfRows = (this.rows-1)/2;

        for (int c = 0; c < this.columns; c++) {
            for (int r = 0; r < this.rows; r++) {
                // Position index of the current cell.
                Position pos = new Position(
                        curX+c-halfCols,
                        curY+r-halfRows);
                TileSquare tile = getOrMakeSquare(pos);
                if (tile == null) {
                    continue;
                }
                tile.setMaxWidth(getWidth()/columns);
                tile.setBuilderTile(r == halfRows && c == halfCols);
                this.add(tile, c, r);
            }
        }
        drawMessageLabels();
    }

    private void drawMessageLabels() {
        getChildren().removeAll(errorPane, successPane);
        add(errorPane, 0, 3);
        add(successPane, 0, 3);
    }

    private TileSquare getOrMakeSquare(Position pos) {
        Tile tile = model.getTile(pos);
        if (tile == null) {
            return null;
        }
        TileSquare square = tileSquareMap.get(pos);
        if (square == null) {
            square = newTileSquare();
            Map<String, Tile> exits = tile.getExits();


            for (Direction direction : Direction.values()) {
                square.setHasExit(direction, exits.containsKey(direction.name()));
            }

            tileSquareMap.put(pos, square);

            square.setExitVisibility(exitsVisible);
            square.setHeightVisibility(heightsVisible);

            updateTileBlocks(pos); // Updates height and top block.
            updateAOSingle(pos);
        }
        return square;
    }

    private void worldMapLoadedHandler(WorldMapLoadedEvent event) {
        resetInternalState();
        System.out.println("map loaded v2");

        drawTilesToGrid();
    }

    private TileSquare newTileSquare() {
        TileSquare tile = new TileSquare();
        return tile;
    }

    private int posToRow(Position pos) {
        return pos.getY()-model.getCurrentPosition().getY()+4;
    }

    private int posToCol(Position pos) {
        return pos.getX()-model.getCurrentPosition().getY()+4;
    }

    private void showErrorMessage(ErrorEvent event) {
        errorLabel.showAndFade(event.getMessage());
    }

    private void showNormalMessage(MessageEvent event) {
        successLabel.showAndFade(event.getMessage());
    }
}
