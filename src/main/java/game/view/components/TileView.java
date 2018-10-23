package game.view.components;

import game.model.BlockType;
import game.model.Direction;

/**
 * Interface for all views of a single tile of the world map.
 */
public interface TileView {
    /**
     * Sets whether the builder is on this tile.
     * @param isBuilderTile True if builder is on the tile, false otherwise.
     */
    void setBuilderTile(boolean isBuilderTile);

    /**
     * Sets the height of this tile.
     * @param height Height.
     */
    void setHeight(int height);

    /**
     * Sets whether an exit exists in the given direction.
     * @param direction Direction of exit.
     * @param hasExit Whether exit is present.
     */
    void setHasExit(Direction direction, boolean hasExit);

    /**
     * Sets the block type of the top block on the tile.
     * @param blockType Top block's type or null if no blocks.
     */
    void setTopBlock(BlockType blockType);

    /**
     * Sets whether exits are to be shown.
     * @param visible Exit visibility.
     */
    void setExitVisibility(boolean visible);

    /**
     * Sets whether heights are to be shown.
     * @param visible Height visibility.
     */
    void setHeightVisibility(boolean visible);
}
