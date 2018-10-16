package game.controller.events;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

public class BlocksChangedEvent extends TileEvent {
    public BlocksChangedEvent(Position position, Tile tile) {
        super(position, tile);
    }
}
