package game.model.events;

import game.model.Direction;

public class BuilderMovedEvent extends BuilderEvent {
    private final Direction direction;

    public BuilderMovedEvent(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
