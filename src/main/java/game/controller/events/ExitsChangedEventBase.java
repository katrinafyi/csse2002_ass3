package game.controller.events;

import csse2002.block.world.Tile;

public class ExitsChangedEventBase extends TileEventBase {
    public ExitsChangedEventBase(Tile tile) {
        super(tile);
    }
}
