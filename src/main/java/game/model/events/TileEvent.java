package game.model.events;

import csse2002.block.world.Position;

public abstract class TileEvent extends BaseBlockWorldEvent {
    private final Position position;

    public TileEvent(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
