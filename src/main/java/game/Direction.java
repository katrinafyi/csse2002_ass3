package game;

import csse2002.block.world.Position;

public enum Direction {
    north(0, -1),
    east(1, 0),
    south(0, 1),
    west(-1, 0);

    private final int x;
    private final int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position asPosition() {
        return new Position(x, y);
    }
}
