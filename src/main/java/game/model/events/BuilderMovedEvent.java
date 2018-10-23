package game.model.events;

import game.model.Direction;

/**
 * Builder moved its current tile.
 */
public class BuilderMovedEvent extends BuilderEvent {
    private final Direction direction;

    /**
     * Construct a new {@link BuilderMovedEvent} after a builder has moved
     * in the given direction.
     * @param direction Direction.
     */
    public BuilderMovedEvent(Direction direction) {
        this.direction = direction;
    }

    /**
     * Gets the direction the builder moved in.
     * @return Direction.
     */
    public Direction getDirection() {
        return direction;
    }
}
