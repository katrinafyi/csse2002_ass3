package game;

import game.model.BlockType;
import game.model.Direction;
import game.view.WorldMapView;
import javafx.scene.Node;
import csse2002.block.world.Position;

import java.util.HashMap;
import java.util.Map;

public class WorldMapGridPane extends UniformGridPane implements WorldMapView {

    private final Map<Position, TileSquare> tileSquareMap = new HashMap<>();

    private Position currentPos;

    public WorldMapGridPane() {
        super(9, 9);
        this.setPrefWidth(500);
    }

    protected Node generateCell(int col, int row) {
        return null;
    }

    @Override
    public void newMapLoaded(Position startingPosition) {
        currentPos = startingPosition;
    }

    @Override
    public void updateTileHeight(Position pos, int height) {

    }

    @Override
    public void updateTileExit(Position pos, Direction direction, boolean hasExit) {

    }

    @Override
    public void moveBuilder(Direction direction, Position newPosition) {
        currentPos = newPosition;
    }

    private int posToRow(Position pos) {
        return pos.getY()-currentPos.getY()+4;
    }

    private int posToCol(Position pos) {
        return pos.getX()-currentPos.getY()+4;
    }

    @Override
    public void updateTopBlock(Position pos, BlockType blockType) {
        if (!tileSquareMap.containsKey(pos)) {
            tileSquareMap.put(pos, new TileSquare());
        }
        tileSquareMap.get(pos).setBlockType(blockType);

        this.add(tileSquareMap.get(blockType), posToCol(pos), posToRow(pos));
    }
}
