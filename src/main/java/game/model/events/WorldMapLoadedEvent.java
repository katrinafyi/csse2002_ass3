package game.model.events;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;

import java.util.Map;

public class WorldMapLoadedEvent extends WorldMapEvent {
    private final Map<Position, Tile> tileMap;

    public WorldMapLoadedEvent(Position position, Map<Position, Tile> tileMap) {
        super(position);
        this.tileMap = tileMap;
    }

    public Map<Position, Tile> getTileMap() {
        return tileMap;
    }
}
