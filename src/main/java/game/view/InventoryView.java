package game.view;

import csse2002.block.world.Block;
import game.model.BlockType;

import java.util.Map;

public interface InventoryView {
    void updateInventory(Map<BlockType, Integer> blocksCount);
}
