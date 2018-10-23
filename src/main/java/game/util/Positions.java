package game.util;

import csse2002.block.world.Position;

/**
 * Operations on {@link Position} objects, treating them as vectors.
 */
public class Positions {
    /**
     * Adds the two positions.
     * @param p1 Position summand.
     * @param p2 Position summand.
     * @return Position representing the component-wise sum.
     */
    public static Position add(Position p1, Position p2) {
        return new Position(p1.getX()+p2.getX(), p1.getY()+p2.getY());
    }

    /**
     * Negates the given position.
     * @param p Position to negate.
     * @return Position representing the negative.
     */
    public static Position neg(Position p) {
        return new Position(-p.getX(), -p.getY());
    }

    /**
     * Subtracts the given positions.
     * @param p1 Left position.
     * @param p2 Right position.
     * @return Component-wise subtraction of {@literal p1 - p2}.
     */
    public static Position sub(Position p1, Position p2) {
        return add(p1, neg(p2));
    }
}
