package game;

import csse2002.block.world.TooLowException;
import game.model.EventDispatcher;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.ErrorEvent;
import game.model.events.WorldMapLoadedEvent;
import game.model.BlockType;
import game.view.TileView;
import csse2002.block.world.Position;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.Map;

public class GameWorldMapView {

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

        this.gridPane.prefWidthProperty().addListener(
        (a, o, n) -> {
            for (Node tile : this.gridPane.getChildren()) {
                ((Region) tile).setMaxWidth(((double) n - (gridPane.COLUMNS - 1) * gridPane.GAP) / gridPane.COLUMNS);
            }
        });
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

        // Change in column index.
        int dc = event.getDirection().asPosition().getX();
        // Change in row index.
        int dr = event.getDirection().asPosition().getY();

        for (int c = 0; c < gridPane.COLUMNS; c++) {
            for (int r = 0; r < gridPane.ROWS; r++) {
                // Position index of the current cell.
                Position pos = new Position(
                        currentPosition.getX()+c,
                        currentPosition.getY()+r);
                TileSquare tile = tileSquareMap.get(pos);
                if (tile == null) {
                    continue;
                }


            }
        }

        currentPosition = event.getNewPosition();
    }

    private boolean onGrid(int col, int row) {
        return (0 <= col && col < gridPane.COLUMNS)
                && (0 <= row && row < gridPane.ROWS);
    }

    private void worldMapLoadedHandler(WorldMapLoadedEvent event) {

        System.out.println("map loaded v2");
        tileSquareMap.clear();
        this.gridPane.getChildren().clear();
        currentPosition = event.getPosition();

        for (Position position : event.getTileMap().keySet()) {
            TileSquare tile = newTileSquare();
            if (position.equals(event.getPosition())) {
                tile.setBuilderTile(true);
            }

            tileSquareMap.put(position, tile);
            this.gridPane.add(tile, posToCol(position), posToRow(position));
        }
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
        System.out.println(pos);
        return pos.getX()-currentPosition.getY()+4;
    }
}
