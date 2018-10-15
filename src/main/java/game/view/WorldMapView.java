package game.view;

import csse2002.block.world.Block;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import game.model.BlockType;
import game.model.Direction;

import java.util.Map;

public interface WorldMapView {
    void newMapLoaded(Position startingPosition);
    void moveBuilder(Direction direction, Position newPosition);
    void setTileHeight(Position pos, int height);
    void setTileHasExit(Position pos, Direction direction, boolean hasExit);
    void setTileTopBlock(Position pos, BlockType blockType);
}
