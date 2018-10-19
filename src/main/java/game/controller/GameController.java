package game.controller;

import csse2002.block.world.Block;
import csse2002.block.world.InvalidBlockException;
import csse2002.block.world.NoExitException;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.TooHighException;
import csse2002.block.world.TooLowException;
import csse2002.block.world.WorldMap;
import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.Utilities;
import game.model.BlockType;
import game.model.BlockWorldModel;
import game.model.Direction;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.ErrorEvent;
import game.model.events.InventoryChangedEvent;
import game.model.events.MessageEvent;
import game.model.events.WorldMapLoadedEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Concrete controller implementation in the MVC structure. Exposes methods
 * for interacting with the block world and internally manipulates the model's
 * state and fires events as necessary.
 *
 * Remark: Classes beginning with Game* are the concrete implementations of the
 * interfaces/abstract classes described by BlockWorld*.
 */
public class GameController
        implements BlockWorldController, MessageController {

    /** Model component. */
    private final BlockWorldModel model;

    /**
     * Construct a new controller interacting with the given model.
     * @param model Block world model.
     */
    public GameController(BlockWorldModel model) {
        this.model = model;
    }

    /**
     * Loads the world map from the given file.
     * @param filePath File path.
     * @throws WorldMapInconsistentException World map is inconsistent.
     * @throws WorldMapFormatException Invalid world map format.
     * @throws FileNotFoundException File does not exist.
     */
    @Override
    public void loadWorldMapFile(String filePath)
            throws WorldMapInconsistentException, WorldMapFormatException,
            FileNotFoundException {
        model.setWorldMap(new WorldMap(filePath));

        // Inform listeners of new and inventory.
        fireMapLoaded();
        fireInventoryChanged();
    }

    /**
     * Saves the world map to the given file.
     * @param filePath File path.
     * @throws IOException Error while saving map.
     */
    @Override
    public void saveWorldMapFile(String filePath) throws IOException {
        model.getWorldMap().saveMap(filePath);
    }

    /**
     * Fires a WorldMapLoadedEvent.
     */
    private void fireMapLoaded() {
        model.notifyListeners(new WorldMapLoadedEvent());
    }

    /**
     * Fires a BlocksChangedEvent.
     * @param position Position whose blocks changed.
     */
    private void fireBlocksChanged(Position position) {
        model.notifyListeners(new BlocksChangedEvent(position));
    }

    /**
     * Fires a BuilderMovedEvent.
     * @param dir Direction the builder moved in.
     */
    private void fireBuilderMoved(Direction dir) {
        model.notifyListeners(new BuilderMovedEvent(dir));
    }

    private void fireInventoryChanged() {
        model.notifyListeners(new InventoryChangedEvent());
    }

    //region  ### Implemented world interaction functions ###
    @Override
    public void moveBuilder(Direction direction) throws NoExitException {
        Tile newTile = model.getBuilder().getCurrentTile()
                .getExits().get(direction.name());
        model.getBuilder().moveTo(newTile);

        model.setCurrentPosition(Utilities.addPos(
                model.getCurrentPosition(), direction.asPosition()));

        fireBuilderMoved(direction);
    }

    @Override
    public void dig() throws InvalidBlockException, TooLowException {
        model.getBuilder().digOnCurrentTile();
        fireBlocksChanged(model.getCurrentPosition());
        fireInventoryChanged();
    }

    @Override
    public void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException {
        model.getBuilder().getCurrentTile().moveBlock(direction.name());

        Position adjacent = Utilities.addPos(model.getCurrentPosition(),
                direction.asPosition());

        // Update the current tile and the tile we moved the block to.
        fireBlocksChanged(model.getCurrentPosition());
        fireBlocksChanged(adjacent);
    }

    @Override
    public void placeBlock(BlockType blockType)
            throws NoSuchElementException, TooHighException {
        List<Block> inventory = model.getBuilder().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (blockType.blockClass.isAssignableFrom(inventory.get(i).getClass())) {
                try {
                    model.getBuilder().dropFromInventory(i);
                } catch (InvalidBlockException e) {
                    throw new AssertionError(e);
                }
                fireInventoryChanged();
                fireBlocksChanged(model.getCurrentPosition());
                return;
            }
        }
        throw new NoSuchElementException(
                "No block of type "+blockType.name()+" in the builder's inventory.");
    }
    //endregion

    @Override
    public void handleErrorMessage(String errorMessage) {
        System.out.println("Firing error event: " + errorMessage);
        model.notifyListeners(new ErrorEvent(errorMessage));
    }

    @Override
    public void handleInfoMessage(String message) {
        model.notifyListeners(new MessageEvent(message));
    }
}
