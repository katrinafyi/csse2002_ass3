package game.controller.events;

import csse2002.block.world.Builder;
import csse2002.block.world.Position;
import game.model.Direction;

public class BuilderMovedEvent extends BuilderEvent {
    private final Position newPosition;
    private final Direction direction;

    public BuilderMovedEvent(Builder builder, Position newPosition, Direction direction) {
        super(builder);
        this.newPosition = newPosition;
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Position getNewPosition() {
        return newPosition;
    }
}
