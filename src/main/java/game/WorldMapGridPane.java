package game;

import game.model.BlockType;
import game.model.Direction;
import game.view.WorldMapView;
import javafx.scene.Node;
import javafx.scene.control.Button;
import csse2002.block.world.Position;

public class WorldMapGridPane extends UniformGridPane implements WorldMapView {
    public WorldMapGridPane() {
        super(9, 9);
        this.setPrefWidth(500);
    }

    protected Node generateCell(int col, int row) {
        Button b = new Button();
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMaxHeight(Double.MAX_VALUE);
        return b;
    }

    @Override
    public void newMapLoaded(Position startingPosition) {

    }

    @Override
    public void updateTileHeight(Position pos, int height) {

    }

    @Override
    public void updateTileExit(Position pos, Direction direction, boolean hasExit) {

    }

    @Override
    public void moveBuilder(Direction direction) {

    }

    @Override
    public void updateTopBlock(Position pos, BlockType blockType) {

    }
}
