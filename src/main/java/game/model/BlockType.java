package game.model;

import csse2002.block.world.Block;
import csse2002.block.world.GrassBlock;
import csse2002.block.world.SoilBlock;
import csse2002.block.world.StoneBlock;
import csse2002.block.world.WoodBlock;

import java.util.HashMap;
import java.util.Map;

public enum BlockType {
    wood(WoodBlock.class),
    grass(GrassBlock.class),
    soil(SoilBlock.class),
    stone(StoneBlock.class);

    public final Class<? extends Block> blockClass;

    private final static Map<Class, BlockType> classToType = new HashMap<>();

    static {
        // Because we can't access static from an enum constructor.
        for (BlockType blockType : BlockType.values()) {
            classToType.put(blockType.blockClass, blockType);
        }
    }

    BlockType(Class<? extends Block> blockClass) {
        this.blockClass = blockClass;
    }

    /**
     * Returns the enum value of the given block instance.
     *
     * @param block Block instance.
     * @return Value from this enum.
     */
    public static BlockType fromBlock(Block block) {
        return classToType.get(block.getClass());
    }
}

