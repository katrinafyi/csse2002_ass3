package game;

import game.model.Direction;
import game.view.AbstractWorldMapView;
import game.view.ErrorView;
import game.view.TileView;
import csse2002.block.world.Position;
import javafx.scene.layout.GridPane;

public class GameWorldMapView extends AbstractWorldMapView implements ErrorView {

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

    @Override
    public void showErrorMessage(String errorMessage) {
        System.out.println("Error handled in view: " + errorMessage);
    }
}
