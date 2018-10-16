package game;

import csse2002.block.world.TooLowException;
import game.controller.EventDispatcher;
import game.controller.events.BaseBlockWorldEvent;
import game.controller.events.BlocksChangedEvent;
import game.controller.events.BuilderMovedEvent;
import game.controller.events.ErrorEvent;
import game.controller.events.WorldMapLoadedEvent;
import game.model.BlockType;
import game.model.Direction;
import game.view.ErrorView;
import game.view.TileView;
import csse2002.block.world.Position;
import javafx.scene.layout.GridPane;

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
        controller.addListener(BaseBlockWorldEvent.class, e -> {
            System.out.println(e);
        });
    }

    private void tileBlocksHandler(BaseBlockWorldEvent e) {
        BlocksChangedEvent event = (BlocksChangedEvent) e;
        try {
            tileSquareMap.get(event.getPosition()).setTopBlock(
                    BlockType.fromBlock(event.getTile().getTopBlock())
            );
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

    private void worldMapLoadedHandler(BaseBlockWorldEvent e) {
        WorldMapLoadedEvent event = (WorldMapLoadedEvent) e;

        System.out.println("map loaded v2");
        tileSquareMap.clear();
        this.gridPane.getChildren().clear();
        currentPosition = event.getPosition();

        for (Position position : event.getTileMap().keySet()) {
            TileSquare tile = newTileSquare();
            tileSquareMap.put(position, tile);
            this.gridPane.add(tile, posToCol(position), posToRow(position));
        }
    }

    private TileSquare newTileSquare() {
        TileSquare tile = new TileSquare();
        tile.maxWidthProperty().bind(
                this.gridPane.widthProperty()
                        .subtract((gridPane.COLUMNS-1)*gridPane.GAP)
                        .divide(gridPane.COLUMNS)
        );
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
