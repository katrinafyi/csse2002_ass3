package game.model;

import csse2002.block.world.Position;

/**
 * Enum representing the four cardinal directions. Defined in the order of
 * N, E, S, W.
 */
public enum Direction {
    /** North enum value. */
    north(0, -1),
    /** East enum value. */
    east(1, 0),
    /** South enum value. */
    south(0, 1),
    /** West enum value. */
    west(-1, 0);

    private final int x;
    private final int y;

    /**
     * Constructs a new Direction.
     * @param x x value of position shift.
     * @param y y value of position shift.
     */
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns this direction as a relative position shift, assuming x+ is right
     * and y+ is down.
     * @return Position object.
     */
    public Position asPosition() {
        return new Position(x, y);
    }
}
