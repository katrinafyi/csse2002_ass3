package game.view;

import csse2002.block.world.Block;

import java.util.Map;

public interface InventoryView {
    void updateInventory(Map<Class<? extends Block>, Integer> blocksCount);
}
