package game;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

public interface BlockWorldView {
    void updateTile(Position position, Tile tile);
    void resetTiles();
}
