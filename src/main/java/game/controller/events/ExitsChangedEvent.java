package game.controller.events;

import csse2002.block.world.Tile;
import csse2002.block.world.Position;

public class ExitsChangedEvent extends TileEvent {
    public ExitsChangedEvent(Position position, Tile tile) {
        super(position, tile);
    }
}
