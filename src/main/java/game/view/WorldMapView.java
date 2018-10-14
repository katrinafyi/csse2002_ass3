package game.view;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

public interface WorldMapView {
    void updateTile(Position position, Tile tile);
    void resetTiles();
}
