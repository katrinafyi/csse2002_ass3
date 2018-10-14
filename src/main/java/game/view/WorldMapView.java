package game.view;

import csse2002.block.world.Block;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import game.model.Direction;

import java.util.Map;

public interface WorldMapView {
    void updateTileHeight(Position pos, int height);
    void updateTileExit(Position pos, Direction direction, boolean hasExit);
    void moveBuilder(Direction direction);
    void updateTopBlock(Position pos, Class<? extends Block> blockClass);
    void updateWorldMap(Map<Position, Tile> positionTileMap);
}
