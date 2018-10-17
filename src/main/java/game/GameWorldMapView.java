package game;

import csse2002.block.world.TooLowException;
import csse2002.block.world.WorldMap;
import game.model.EventDispatcher;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.ErrorEvent;
import game.model.events.WorldMapEvent;
import game.model.events.WorldMapLoadedEvent;
import game.model.BlockType;
import game.view.TileView;
import csse2002.block.world.Position;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class GameWorldMapView {

    private final UniformGridPane gridPane;
    private final Map<Position, TileView> tileSquareMap = new HashMap<>();
    private Position currentPosition;

    public GameWorldMapView(EventDispatcher<BaseBlockWorldEvent> controller) {
        gridPane = new UniformGridPane(9, 9, 2);
        gridPane.setPrefWidth(500);

        controller.addListener(WorldMapLoadedEvent.class, this::worldMapLoadedHandler);
        controller.addListener(BuilderMovedEvent.class, this::setPositionHandler);
        controller.addListener(ErrorEvent.class, this::errorHandler);
        controller.addListener(BlocksChangedEvent.class, this::tileBlocksHandler);
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

    private void tileBlocksHandler(BlocksChangedEvent event) {
        try {
            TileView tile = tileSquareMap.get(event.getPosition());
            tile.setTopBlock(BlockType.fromBlock(event.getTile().getTopBlock()));
            tile.setHeight(event.getTile().getBlocks().size());
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

    private void setPositionHandler(BaseBlockWorldEvent e) {
        BuilderMovedEvent event = (BuilderMovedEvent) e;
        currentPosition = event.getNewPosition();
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
