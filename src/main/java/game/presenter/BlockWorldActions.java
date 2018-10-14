package game.presenter;

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

public interface BlockWorldActions {
    void loadWorldMapFile(String filePath)
            throws WorldMapInconsistentException, WorldMapFormatException,
            FileNotFoundException;

    void moveBuilder(Direction direction) throws NoExitException;

    void dig() throws InvalidBlockException, TooLowException;

    void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException;

    void dropBlock(BlockType blockType)
                    throws NoSuchElementException, TooHighException, InvalidBlockException;
}
