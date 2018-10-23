package game.model;

import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of the game's model.
 */
public class GameModel extends BlockWorldModel {
    /** Current world map. */
    private WorldMap worldMap;
    /** Current builder's position. */
    private Position currentPosition;

    /**
     * Gets the current world map.
     * @return World map.
     */
    @Override
    public WorldMap getWorldMap() {
        return worldMap;
    }

    /**
     * Sets the current world map to the given map and updates the current
     * position to the map's starting position.
     * @param worldMap New world map.
     */
    @Override
    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
        setCurrentPosition(worldMap.getStartPosition());
    }

    /**
     * Gets the builder's current position.
     * @return Position.
     */
    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Sets the builder's current position.
     * @param currentPosition New current position.
     */
    @Override
    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

}

