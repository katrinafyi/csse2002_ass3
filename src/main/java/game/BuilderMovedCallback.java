package game;

import csse2002.block.world.Position;

public interface BuilderMovedCallback {
    void callback(Position oldPosition, Position newPosition);
}
