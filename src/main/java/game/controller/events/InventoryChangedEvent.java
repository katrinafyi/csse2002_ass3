package game.controller.events;

import csse2002.block.world.Builder;
import game.model.BlockType;

import java.util.Map;

public class InventoryChangedEvent extends BuilderEvent {
    private final Map<BlockType, Integer> blocksCount;

    public InventoryChangedEvent(Builder builder,
                                 Map<BlockType, Integer> blocksCount) {
        super(builder);
        this.blocksCount = blocksCount;
    }

    public Map<BlockType, Integer> getBlocksCount() {
        return blocksCount;
    }
}
