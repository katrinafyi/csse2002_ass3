package game.model.events;

import csse2002.block.world.Position;

/**
 * Superclass for events relating to a particular tile.
 */
public abstract class TileEvent extends BaseBlockWorldEvent {
    private final Position position;

    /**
     * Constructs a new {@link TileEvent} regarding the tile at the given
     * position.
     * @param position Position of tile.
     */
    public TileEvent(Position position) {
        this.position = position;
    }

    /**
     * Gets the position of the tile.
     * @return Position.
     */
    public Position getPosition() {
        return position;
    }
}
