package game.model.events;

import csse2002.block.world.Position;

public class WorldMapEvent extends BaseBlockWorldEvent {
    private final Position position;

    public WorldMapEvent(Position position) {

        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}

