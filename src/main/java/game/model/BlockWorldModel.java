package game.model;

import csse2002.block.world.Block;
import csse2002.block.world.Builder;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;
import game.model.events.BaseBlockWorldEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract superclass of the model component for the block world game.*
 * Handles event dispatch and provides some shortcut methods (marked final)
 * to access common getters.
 *
 * <p>Implementing classes should manage the state of the world map, as this
 * abstract base class is stateless.</p>
 *
 * <p>This is an abstract class because it needs to extend {@link EventDispatcher}.
 * In MVC, classes which use the model always need both its state and events
 * so it is reasonable to tie them together.</p>
 */
public abstract class BlockWorldModel extends EventDispatcher<BaseBlockWorldEvent> {
    /**
     * Gets the current {@link WorldMap}.
     * @return World map.
     */
    public abstract WorldMap getWorldMap();

    /**
     * Sets the world map to the one given, overwriting any previous one.
     * @param worldMap New world map.
     */
    public abstract void setWorldMap(WorldMap worldMap);

    /**
     * Gets the position the builder is currently at.
     * @return Builder's current position.
     */
    public abstract Position getCurrentPosition();

    /**
     * Sets the position the builder is currently at.
     * @param currentPosition Builder's current position.
     */
    public abstract void setCurrentPosition(Position currentPosition);

    // This method and ones later below are marked final because they are
    // only shortcuts to access getters using other abstract methods.
    // Implementing classes should never need to change them, so this ensures
    // they are equivalent to calling each getter in turn.

    /**
     * Gets the builder associated with the current world map.
     * @return Builder.
     */
    public final Builder getBuilder() {
        return getWorldMap().getBuilder();
    }

    /**
     * Returns the tile the builder is currently on.
     * @return Tile.
     */
    public final Tile getCurrentTile() {
        return getWorldMap().getBuilder().getCurrentTile();
    }

    /**
     * Gets the tile at the given position.
     * @param position Position of tile.
     * @return Tile at position or null if no such tile exists.
     */
    public final Tile getTile(Position position) {
        return getWorldMap().getTile(position);
    }

    /**
     * Computes and returns a mapping representing the count of each block
     * type in the builder's current inventory.
     *
     * If the builder has none of a given block, the key is still present with
     * a value of 0.
     * @return Mapping of block counts.
     */
    public final Map<BlockType, Integer> getInventoryCount() {
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
