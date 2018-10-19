package game.model;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;

import java.util.HashMap;
import java.util.Map;

public class GameModel extends BlockWorldModel {
    private WorldMap worldMap;
    private Position currentPosition;
    private final Map<Position, Tile> tileMap = new HashMap<>();

    @Override
    public WorldMap getWorldMap() {
        return worldMap;
    }

    @Override
    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
        setCurrentPosition(worldMap.getStartPosition());
    }

    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

}

