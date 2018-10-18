package game.model;

import csse2002.block.world.Block;
import csse2002.block.world.Builder;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;
import game.model.events.BaseBlockWorldEvent;
import javafx.event.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BlockWorldModel extends EventDispatcher<BaseBlockWorldEvent> {
    public abstract WorldMap getWorldMap();

    public Builder getBuilder() {
        return getWorldMap().getBuilder();
    }

    public abstract void setWorldMap(WorldMap worldMap);

    public abstract Position getCurrentPosition();

    public abstract void setCurrentPosition(Position currentPosition);

    public Tile getAdjacentTile(Tile tile, Direction direction) {
        return tile.getExits().get(direction.name());
    }

    public abstract Map<Position, Tile> getTileMap();

    public Map<BlockType, Integer> getInventoryCount() {
        Map<BlockType, Integer> countMap = new HashMap<>();
        for (BlockType type : BlockType.values()) {
            countMap.put(type, 0);
        }
        for (Block block : getBuilder().getInventory()) {
            BlockType type = BlockType.fromBlock(block);
            countMap.put(type, countMap.get(type) + 1);
        }
        return Collections.unmodifiableMap(countMap);
    }
}
