package game.view;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.TooLowException;
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
import game.view.components.TileView;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorldMapView extends UniformGridPane {

    private final List<TileSquare> visibleTileSquares = new ArrayList<>();

    private final Map<Position, TileSquare> tileSquareMap = new HashMap<>();
    private final Cache<Position, Integer> tileHeights = new Cache<>(this::getTileHeight);
    private final BlockWorldModel model;

    private final FadingLabel successLabel;
    private final FadingLabel errorLabel;

    public GameWorldMapView(BlockWorldModel model) {
        super(9, 9, 2);

        this.model = model;

        this.setPrefWidth(500);

        errorLabel = new FadingLabel(Duration.seconds(1), Duration.millis(500));
        setMessageLabelStyle(errorLabel, "#911414");
        successLabel = new FadingLabel(Duration.seconds(1), Duration.millis(500));
        setMessageLabelStyle(successLabel, "#167708");

        model.addListener(WorldMapLoadedEvent.class, this::worldMapLoadedHandler);
        model.addListener(BuilderMovedEvent.class, this::builderMovedHandler);
        model.addListener(BlocksChangedEvent.class, this::blocksChangedHandler);
        model.addListener(null, this::allHandler);

        model.addListener(ErrorEvent.class, this::showErrorMessage);
        model.addListener(MessageEvent.class, this::showNormalMessage);

        this.setMaxWidth(Control.USE_PREF_SIZE);
        this.setMaxHeight(Control.USE_PREF_SIZE);

        this.prefWidthProperty().addListener(this::setCellWidths);

        drawMessageLabels();
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

    private void setCellWidths(ObservableValue<? extends Number> prop,
                               Number oldValue, Number newValue) {
        for (TileSquare tile : visibleTileSquares) {
            tile.setMaxWidth(
                    ((double)newValue - (this.COLUMNS - 1) * this.GAP)
                    / this.COLUMNS);
        }
    }

    private int getTileHeight(Position position) {
        Tile tile = model.getTileMap().get(position);
        if (tile == null) {
            return 0;
        }
        return model.getTileMap().get(position).getBlocks().size();
    }

    private void allHandler(BaseBlockWorldEvent event) {
        System.out.println("View caught: " + event);
    }

    private void blocksChangedHandler(BlocksChangedEvent event) {
        Position position = event.getPosition();
        TileView tile = tileSquareMap.get(position);

        int height = event.getTile().getBlocks().size();
        tile.setHeight(height);

        try {
            tile.setTopBlock(height == 0
                    ? null : BlockType.fromBlock(event.getTile().getTopBlock()));
        } catch (TooLowException e) {
            throw new AssertionError(e);
        }
        tileHeights.put(position, height);

        updateAOAllNeighbours(position);
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
        removeTilesFromGrid();
        drawTilesToGrid();
    }

    private void resetInternalState() {
        this.getChildren().clear();
        removeTilesFromGrid();
        tileSquareMap.clear();
    }

    private void removeTilesFromGrid() {
        getChildren().clear();
        visibleTileSquares.clear();
    }

    private void drawTilesToGrid() {
        Position current = model.getCurrentPosition();
        int curX = current.getX();
        int curY = current.getY();

        for (int c = 0; c < this.COLUMNS; c++) {
            for (int r = 0; r < this.ROWS; r++) {
                // Position index of the current cell.
                Position pos = new Position(
                        curX+c-this.HALF_COLS,
                        curY+r-this.HALF_ROWS);
                TileSquare tile = getOrMakeSquare(pos);
                if (tile == null) {
                    continue;
                }
                tile.setBuilderTile(r == this.HALF_ROWS && c == this.HALF_COLS);
                this.add(tile, c, r);
                visibleTileSquares.add(tile);
            }
        }
        drawMessageLabels();
    }

    private void drawMessageLabels() {
        add(errorLabel, 1, 3, 7, 1);
        add(successLabel, 1, 3, 7, 1);
    }

    private TileSquare getOrMakeSquare(Position pos) {
        if (!model.getTileMap().containsKey(pos)) {
            return null;
        }
        TileSquare square = tileSquareMap.get(pos);
        if (square == null) {
            square = newTileSquare();
            Map<String, Tile> exits = model.getTileMap().get(pos).getExits();

            for (Direction direction : Direction.values()) {
                square.setHasExit(direction, exits.containsKey(direction.name()));
            }
            tileSquareMap.put(pos, square);
        }
        return square;
    }

    private void worldMapLoadedHandler(WorldMapLoadedEvent event) {
        resetInternalState();
        System.out.println("map loaded v2");

        drawTilesToGrid();
        setCellWidths(this.prefWidthProperty(), 0, this.getWidth());
    }

    private TileSquare newTileSquare() {
        TileSquare tile = new TileSquare();
        tile.setMaxWidth(10);
        return tile;
    }

    private int posToRow(Position pos) {
        return pos.getY()-model.getCurrentPosition().getY()+4;
    }

    private int posToCol(Position pos) {
        return pos.getX()-model.getCurrentPosition().getY()+4;
    }

    public void showErrorMessage(ErrorEvent event) {
        errorLabel.showAndFade(event.getMessage());
    }

    private void showNormalMessage(MessageEvent event) {
        successLabel.showAndFade(event.getMessage());
    }
}
