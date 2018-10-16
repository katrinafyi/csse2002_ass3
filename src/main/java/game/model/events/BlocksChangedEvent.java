package game.model.events;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

/**
 * The blocks on some tile were altered.
 */
public class BlocksChangedEvent extends TileEvent {
    /**
     * Construct a new event
     * @param position Position of changed tile.
     * @param tile Tile whose blocks changed.
     */
    public BlocksChangedEvent(Position position, Tile tile) {
        super(position, tile);
    }
}
