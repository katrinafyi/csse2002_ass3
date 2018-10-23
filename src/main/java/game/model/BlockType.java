package game.model;

import csse2002.block.world.Block;
import csse2002.block.world.GrassBlock;
import csse2002.block.world.SoilBlock;
import csse2002.block.world.StoneBlock;
import csse2002.block.world.WoodBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing all the valid block types defined in the game.
 *
 * This enum isn't strictly necessary, we could have used
 * {@code Class<? extends Block>} everywhere this enum is used, but that's
 * not as neat.
 */
public enum BlockType {
    /** Wood block type. */
    wood(WoodBlock.class),
    /** Grass block type. */
    grass(GrassBlock.class),
    /** Soil block type. */
    soil(SoilBlock.class),
    /** Stone block type. */
    stone(StoneBlock.class);

    /** Class of the block type. */
    private final Class<? extends Block> blockClass;

    /** Map of classes to enum values. */
    private final static Map<Class, BlockType> classToType = new HashMap<>();
    static {
        // Because we can't access static from an enum constructor.
        for (BlockType blockType : BlockType.values()) {
            classToType.put(blockType.blockClass, blockType);
        }
    }

    /**
     * New BlockType value.
     * @param blockClass Block's class type.
     */
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

