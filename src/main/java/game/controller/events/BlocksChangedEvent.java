package game.controller.events;

import csse2002.block.world.Tile;

public class BlocksChangedEvent extends TileEventBase {
    public BlocksChangedEvent(Tile tile) {
        super(tile);
    }
}
