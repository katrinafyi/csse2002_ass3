package game.model.events;

import csse2002.block.world.Position;

/**
 * The blocks on some tile were altered.
 */
public class BlocksChangedEvent extends TileEvent {
    /**
     * Construct a new event
     * @param position Position of changed tile.
     */
    public BlocksChangedEvent(Position position) {
        super(position, tile);
    }
}
