package game;

import csse2002.block.world.Block;
import csse2002.block.world.GrassBlock;
import csse2002.block.world.SoilBlock;
import csse2002.block.world.StoneBlock;
import csse2002.block.world.WoodBlock;

import java.util.HashMap;
import java.util.Map;

public enum BlockTypes {
    wood(WoodBlock.class),
    grass(GrassBlock.class),
    soil(SoilBlock.class),
    stone(StoneBlock.class);

    public final Class<? extends Block> blockClass;

    private final static Map<Class, BlockTypes> classToType = new HashMap<>();

    static {
        // Because we can't access static from an enum constructor.
        for (BlockTypes blockType : BlockTypes.values()) {
            classToType.put(blockType.blockClass, blockType);
        }
    }

    BlockTypes(Class<? extends Block> blockClass) {
        this.blockClass = blockClass;
    }

    /**
     * Instantiates a new object of the block's type.
     * @return New block object.
     */
    public Block newInstance() {
        try {
            return blockClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // The enum values we define should never result in these
            // exceptions.
            throw new AssertionError(
                    "Exception while instantiating block class.", e);
        }
    }

    /**
     * Returns the enum value corresponding to a given block instance's
     * type.
     * @param block Block instance.
     * @return BlockTypes enum value.
     */
    public static BlockTypes fromInstance(Block block) {
        return classToType.get(block.getClass());
    }

}

