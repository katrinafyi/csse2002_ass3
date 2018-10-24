package game.model;

import csse2002.block.world.Position;
import csse2002.block.world.WorldMap;

/**
 * Abstract model component with methods for setting world map and current
 * position. Only the controller should require this class.
 *
 * @see ReadOnlyBlockWorldModel
 */
public abstract class ModifiableBlockWorldModel extends ReadOnlyBlockWorldModel {
    /**
     * Sets the world map to the one given, overwriting any previous one.
     * @param worldMap New world map.
     */
    public abstract void setWorldMap(WorldMap worldMap);

    /**
     * Sets the position the builder is currently at.
     * @param currentPosition Builder's current position.
     */
    public abstract void setCurrentPosition(Position currentPosition);
}
