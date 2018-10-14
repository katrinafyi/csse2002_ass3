package game.view;

import csse2002.block.world.Block;

public interface InventoryView {
    void updateCount(Class<? extends Block> blockType, int count);
}
