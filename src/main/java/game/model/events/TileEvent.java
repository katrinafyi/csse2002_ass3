package game.model.events;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

public abstract class TileEvent extends WorldMapEvent {
    public TileEvent(Position position) {
        super(position);
    }
}
