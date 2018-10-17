package game.model;

import csse2002.block.world.Builder;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;

import java.util.Map;

public interface BlockWorldModel {
    WorldMap getWorldMap();

    Builder getBuilder();

    void setWorldMap(WorldMap worldMap);

    Position getCurrentPosition();

    void setCurrentPosition(Position currentPosition);

    Map<Position, Tile> getTileMap();

    Map<BlockType, Integer> getInventoryCount();
}
