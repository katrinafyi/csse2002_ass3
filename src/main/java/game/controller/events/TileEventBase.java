package game.controller.events;

import csse2002.block.world.Tile;

public class TileEventBase extends BaseBlockWorldEvent {
    private final Tile tile;

    public TileEventBase(Tile tile) {

        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }
}
