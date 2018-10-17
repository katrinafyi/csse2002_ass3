package game.controller;

import csse2002.block.world.InvalidBlockException;
import csse2002.block.world.NoExitException;
import csse2002.block.world.TooHighException;
import csse2002.block.world.TooLowException;
import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.model.BlockType;
import game.model.Direction;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

/**
 * Interface describing the controller component. Offers methods to
 * perform actions on a world map.
 */
public interface BlockWorldController {
    /**
     * Load the world map at the given path.
     * @param filePath File path.
     * @throws WorldMapInconsistentException Map is geometrically inconsistent.
     * @throws WorldMapFormatException Map format is invalid.
     * @throws FileNotFoundException File not found.
     */
    void loadWorldMapFile(String filePath)
            throws WorldMapInconsistentException, WorldMapFormatException,
            FileNotFoundException;

    /**
     * Attempts to move the builder in the direction given.
     * @param direction Direction.
     * @throws NoExitException If builder cannot move in that direction
     *      (no exit or incompatible tile heights).
     */
    void moveBuilder(Direction direction) throws NoExitException;

    /**
     * Attempt to dig on the current tile and store the block in the inventory.
     * @throws InvalidBlockException Block is not diggable.
     * @throws TooLowException No blocks on tile.
     */
    void dig() throws InvalidBlockException, TooLowException;

    /**
     * Moves the top block of the current tile in the direction given.
     * @param direction Direction.
     * @throws NoExitException No exit in that direction.
     * @throws InvalidBlockException Block not moveable.
     * @throws TooHighException Adjacent tile is not lower than this one.
     */
    void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException;

    /**
     * Places a block from the inventory onto the current tile.
     * @param blockType Block type to place.
     * @throws NoSuchElementException No blocks of the given type held.
     * @throws TooHighException Tile already contains max blocks.
     */
    void placeBlock(BlockType blockType)
                    throws NoSuchElementException, TooHighException;
}
