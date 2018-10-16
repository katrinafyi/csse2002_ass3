package game.controller.events;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

public class TileEvent extends WorldMapEvent {
    private final Tile tile;

    public TileEvent(Position position, Tile tile) {
        super(position);
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

}
