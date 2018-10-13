package game;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;

import java.util.Map;

public interface MapLoadedCallback {
    void call(WorldMap newMap, Map<Position, Tile> positionTileMap);
}
