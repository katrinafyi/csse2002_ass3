package game;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

public interface TileChangedCallback {
    void call(Tile tile);
}
