package game;

import csse2002.block.world.Position;

public interface BuilderMovedCallback {
    void call(Position oldPosition, Position newPosition);
}
