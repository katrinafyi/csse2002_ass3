package game.model.events;

import csse2002.block.world.Builder;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import game.model.Direction;

public class BuilderMovedEvent extends BuilderEvent {
    private final Direction direction;
    private final Position oldPosition;

    public BuilderMovedEvent(Direction direction, Position oldPosition) {
        this.oldPosition = oldPosition;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Position getOldPosition() {
        return oldPosition;
    }
}