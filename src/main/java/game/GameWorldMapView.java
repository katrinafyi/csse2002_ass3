package game;

import game.controller.BlockWorldActions;
import game.model.BlockType;
import game.model.Direction;
import game.view.AbstractWorldMapView;
import game.view.TileView;
import game.view.WorldMapView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import csse2002.block.world.Position;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.Map;

public class GameWorldMapView extends AbstractWorldMapView {

    private final UniformGridPane gridPane;

    public GameWorldMapView() {
        gridPane = new UniformGridPane(9, 9, 2);
        gridPane.setPrefWidth(500);
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    @Override
    protected TileView newTileView(Position position) {
        TileSquare tile = new TileSquare();
        tile.maxWidthProperty().bind(
                this.gridPane.widthProperty()
                        .subtract((gridPane.COLUMNS-1)*gridPane.GAP)
                        .divide(gridPane.COLUMNS)
        );
        this.gridPane.add(tile, posToCol(position), posToRow(position));
        return tile;
    }

    @Override
    public void newMapLoaded(Position pos) {
        super.newMapLoaded(pos);
        this.gridPane.getChildren().clear();
    }

    @Override
    public void moveBuilder(Direction direction, Position newPosition) {
        super.moveBuilder(direction, newPosition);
    }

    private int posToRow(Position pos) {
        return pos.getY()-getCurrentPosition().getY()+4;
    }

    private int posToCol(Position pos) {
        System.out.println(pos);
        return pos.getX()-getCurrentPosition().getY()+4;
    }
}
