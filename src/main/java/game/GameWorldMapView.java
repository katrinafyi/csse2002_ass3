package game;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.TooLowException;
import game.model.BlockType;
import game.model.Direction;
import game.model.EventDispatcher;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.ErrorEvent;
import game.model.events.WorldMapLoadedEvent;
import game.view.TileView;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorldMapView {

    private final List<TileSquare> visibleTileSquares = new ArrayList<>();

    private final UniformGridPane gridPane;
    private final Map<Position, TileSquare> tileSquareMap = new HashMap<>();
    private Position currentPosition;

    public GameWorldMapView(EventDispatcher<BaseBlockWorldEvent> controller) {
        gridPane = new UniformGridPane(9, 9, 2);
        gridPane.setPrefWidth(500);

        controller.addListener(WorldMapLoadedEvent.class, this::worldMapLoadedHandler);
        controller.addListener(BuilderMovedEvent.class, this::builderMovedHandler);
        controller.addListener(ErrorEvent.class, this::errorHandler);
        controller.addListener(BlocksChangedEvent.class, this::blocksChangedHandler);
        controller.addListener(null, this::allHandler);

        this.gridPane.setMaxWidth(Control.USE_PREF_SIZE);

        this.gridPane.setMaxHeight(Control.USE_PREF_SIZE);

        this.gridPane.prefWidthProperty().addListener(this::setCellWidths);
    }

    private void setCellWidths(ObservableValue<? extends Number> prop,
                               Number oldValue, Number newValue) {
        for (TileSquare tile : visibleTileSquares) {
            tile.setMaxWidth(
                    ((double)newValue - (gridPane.COLUMNS - 1) * gridPane.GAP)
                    / gridPane.COLUMNS);
        }
    }

    private void allHandler(BaseBlockWorldEvent e) {
        System.out.println(e);
    }

    private void blocksChangedHandler(BlocksChangedEvent event) {
        try {
            TileView tile = tileSquareMap.get(event.getPosition());
            int height = event.getTile().getBlocks().size();
            tile.setHeight(height);
            tile.setTopBlock(height == 0
                    ? null : BlockType.fromBlock(event.getTile().getTopBlock()));
        } catch (TooLowException e1) {
            e1.printStackTrace();
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    private void errorHandler(BaseBlockWorldEvent e) {
        ErrorEvent event = (ErrorEvent) e;
        System.out.println(event);
    }

    private void builderMovedHandler(BuilderMovedEvent event) {
        Position oldPosition = currentPosition;

        removeTilesFromGrid();
        currentPosition = event.getNewPosition();
        drawTilesToGrid();
    }

    private void resetInternalState() {

    }

    private void removeTilesFromGrid() {
        gridPane.getChildren().removeAll(visibleTileSquares);
        visibleTileSquares.clear();
    }

    private void drawTilesToGrid() {
        for (int c = 0; c < gridPane.COLUMNS; c++) {
            for (int r = 0; r < gridPane.ROWS; r++) {
                // Position index of the current cell.
                Position pos = new Position(
                        currentPosition.getX()+c-gridPane.HALF_COLS,
                        currentPosition.getY()+r-gridPane.HALF_ROWS);
                TileSquare tile = tileSquareMap.get(pos);
                if (tile == null) {
                    continue;
                }
                tile.setBuilderTile(r == gridPane.HALF_ROWS && c == gridPane.HALF_COLS);
                gridPane.add(tile, c, r);
                visibleTileSquares.add(tile);
            }
        }

    }

    private void worldMapLoadedHandler(WorldMapLoadedEvent event) {

        System.out.println("map loaded v2");
        tileSquareMap.clear();
        this.gridPane.getChildren().clear();
        currentPosition = event.getPosition();

        for (Map.Entry<Position, Tile> pair : event.getTileMap().entrySet()) {
            Position position = pair.getKey();
            Map<String, Tile> exits = pair.getValue().getExits();
            TileSquare tileSq = newTileSquare();

            for (Direction direction : Direction.values()) {
                tileSq.setHasExit(direction, exits.containsKey(direction.name()));
            }
            tileSquareMap.put(position, tileSq);
        }
        drawTilesToGrid();
        setCellWidths(gridPane.prefWidthProperty(), 0, gridPane.getWidth());
    }

    private TileSquare newTileSquare() {
        TileSquare tile = new TileSquare();
        tile.setMaxWidth(10);

        return tile;
    }

    private int posToRow(Position pos) {
        return pos.getY()-currentPosition.getY()+4;
    }

    private int posToCol(Position pos) {
        return pos.getX()-currentPosition.getY()+4;
    }
}
