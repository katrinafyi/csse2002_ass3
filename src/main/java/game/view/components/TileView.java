package game.view.components;

import game.model.BlockType;
import game.model.Direction;

public interface TileView {
    void setBuilderTile(boolean isBuilderTile);
    void setHeight(int height);
    void setHasExit(Direction direction, boolean hasExit);
    void setTopBlock(BlockType blockType);
    void setExitVisibility(boolean visible);
    void setHeightVisibility(boolean visible);
}
