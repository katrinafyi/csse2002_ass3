package game.controller.events;

import csse2002.block.world.Tile;

public class ExitsChangedEvent extends TileEventBase {
    public ExitsChangedEvent(Tile tile) {
        super(tile);
    }
}
