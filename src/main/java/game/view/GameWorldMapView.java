package game.view;

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
import game.view.components.FadingLabel;
import game.view.components.TileSquare;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorldMapView extends UniformGridPane {

    private final List<TileSquare> visibleTileSquares = new ArrayList<>();

    private Map<Position, Tile> allTiles;
    private final Map<Position, TileSquare> tileSquareMap = new HashMap<>();
    private Position currentPosition;

    private final FadingLabel errorLabel;

    public GameWorldMapView(EventDispatcher<BaseBlockWorldEvent> model) {
        super(9, 9, 2);
        this.setPrefWidth(500);

        errorLabel = new FadingLabel(Duration.seconds(1), Duration.millis(500));
        errorLabel.setPadding(new Insets(10));
        errorLabel.setStyle(
                "-fx-font-size: 15;"
                + "-fx-font-weight: bold;"
                +"-fx-text-fill: white;"
                + "-fx-background-color: #911414;"
                + "-fx-border-radius: 5;"
        );
        errorLabel.setOpacity(0);
        GridPane.setHalignment(errorLabel, HPos.CENTER);

        model.addListener(WorldMapLoadedEvent.class, this::worldMapLoadedHandler);
        model.addListener(BuilderMovedEvent.class, this::builderMovedHandler);
        model.addListener(BlocksChangedEvent.class, this::blocksChangedHandler);
        model.addListener(null, this::allHandler);

        model.addListener(ErrorEvent.class, this::showErrorMessage);

        this.setMaxWidth(Control.USE_PREF_SIZE);
        this.setMaxHeight(Control.USE_PREF_SIZE);

        this.prefWidthProperty().addListener(this::setCellWidths);
    }

    private void setCellWidths(ObservableValue<? extends Number> prop,
                               Number oldValue, Number newValue) {
        for (TileSquare tile : visibleTileSquares) {
            tile.setMaxWidth(
                    ((double)newValue - (this.COLUMNS - 1) * this.GAP)
                    / this.COLUMNS);
        }
    }

    private void allHandler(BaseBlockWorldEvent e) {
        System.out.println("View caught: " + e);
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

    private void builderMovedHandler(BuilderMovedEvent event) {
        Position oldPosition = currentPosition;

        removeTilesFromGrid();
        currentPosition = event.getNewPosition();
        drawTilesToGrid();
    }

    private void resetInternalState() {
        this.getChildren().clear();
        removeTilesFromGrid();
        tileSquareMap.clear();
        currentPosition = null;
    }

    private void removeTilesFromGrid() {
        getChildren().removeAll(visibleTileSquares);
        getChildren().remove(errorLabel);
        visibleTileSquares.clear();
    }

    private void drawTilesToGrid() {
        for (int c = 0; c < this.COLUMNS; c++) {
            for (int r = 0; r < this.ROWS; r++) {
                // Position index of the current cell.
                Position pos = new Position(
                        currentPosition.getX()+c-this.HALF_COLS,
                        currentPosition.getY()+r-this.HALF_ROWS);
                TileSquare tile = getOrMakeSquare(pos);
                if (tile == null) {
                    continue;
                }
                tile.setBuilderTile(r == this.HALF_ROWS && c == this.HALF_COLS);
                this.add(tile, c, r);
                visibleTileSquares.add(tile);
            }
        }
        add(errorLabel, 2, 3, 5, 1);
    }

    private TileSquare getOrMakeSquare(Position pos) {
        if (!allTiles.containsKey(pos)) {
            return null;
        }
        TileSquare square = tileSquareMap.get(pos);
        if (square == null) {
            square = newTileSquare();
            Map<String, Tile> exits = allTiles.get(pos).getExits();

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
        currentPosition = event.getPosition();

        allTiles = event.getTileMap();

        drawTilesToGrid();
        setCellWidths(this.prefWidthProperty(), 0, this.getWidth());
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

    public void showErrorMessage(ErrorEvent event) {
        errorLabel.showAndFade(event.getMessage());
    }
}
