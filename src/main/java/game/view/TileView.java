package game.view;

import game.model.BlockType;
import game.model.Direction;

public interface TileView {
    void setBuilderTile(boolean isBuilderTile);
    void setHeight(int height);
    void setHasExit(Direction direction, boolean hasExit);
    void setTopBlock(BlockType blockType);
    void setTooHighOrLow(boolean tooHighOrLow);
}
