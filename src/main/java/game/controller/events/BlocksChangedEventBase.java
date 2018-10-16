package game.controller.events;

import csse2002.block.world.Tile;

public class BlocksChangedEventBase extends TileEventBase {
    public BlocksChangedEventBase(Tile tile) {
        super(tile);
    }
}
