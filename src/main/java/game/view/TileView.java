package game.view;

import game.model.BlockType;
import game.model.Direction;

public interface TileView {
    void updateHeight(int height);
    void updateExit(Direction direction, boolean hasExit);
    void updateTopBlock(BlockType blockType);
}
