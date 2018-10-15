package game.view;

import csse2002.block.world.Position;
import game.Utilities;
import game.model.BlockType;
import game.model.Direction;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWorldMapView implements WorldMapView {
    protected final Map<Position, TileView> tileViewMap = new HashMap<>();

    private Position currentPosition;

    protected Position getCurrentPosition() {
        return currentPosition;
    }

    protected void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    protected abstract TileView newTileView();

    private TileView getOrInsertTile(Position pos) {
        TileView tile = tileViewMap.get(pos);
        if (tile == null) {
            tile = newTileView();
            tileViewMap.put(pos, tile);
        }
        return tile;
    }


    @Override
    public void newMapLoaded(Position startingPosition) {
        setCurrentPosition(startingPosition);
    }

    @Override
    public void updateTileHeight(Position pos, int height) {
        getOrInsertTile(pos).updateHeight(height);
    }

    @Override
    public void updateTileExit(Position pos, Direction direction, boolean hasExit) {
        getOrInsertTile(pos).updateExit(direction, hasExit);
    }

    @Override
    public void moveBuilder(Direction direction, Position newPosition) {
        setCurrentPosition(newPosition);
    }

    @Override
    public void updateTopBlock(Position pos, BlockType blockType) {
        getOrInsertTile(pos).updateTopBlock(blockType);
    }
}
