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
import game.model.BlockType;
import game.model.BlockWorldModel;
import game.model.Direction;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.ErrorEvent;
import game.model.events.InventoryChangedEvent;
import game.model.events.MessageEvent;
import game.model.events.WorldMapLoadedEvent;
import game.util.Positions;

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

    /**
     * Fires an {@link InventoryChangedEvent}.
     */
    private void fireInventoryChanged() {
        model.notifyListeners(new InventoryChangedEvent());
    }

    //region  ### Implemented world interaction functions ###

    /**
     * Moves the builder in the given direction.
     * @param direction Direction.
     * @throws NoExitException No exit or heights incompatible.
     */
    @Override
    public void moveBuilder(Direction direction) throws NoExitException {
        Tile newTile = model.getCurrentTile()
                .getExits().get(direction.name());
        model.getBuilder().moveTo(newTile);

        model.setCurrentPosition(Positions.add(
                model.getCurrentPosition(), direction.asPosition()));

        fireBuilderMoved(direction);
    }

    /**
     * Digs on the current tile and if the block can be carried, adds it to the
     * builder's inventory.
     * @throws InvalidBlockException Block can't be dug.
     * @throws TooLowException No blocks on tile.
     */
    @Override
    public void dig() throws InvalidBlockException, TooLowException {
        model.getBuilder().digOnCurrentTile();
        fireBlocksChanged(model.getCurrentPosition());
        fireInventoryChanged();
    }

    /**
     * Moves the top block of the current tile in the given direction.
     * @param direction Direction.
     * @throws NoExitException No exit in that direction.
     * @throws InvalidBlockException Block can't be moved.
     * @throws TooHighException Other tile is not lower than current one.
     */
    @Override
    public void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException {
        model.getCurrentTile().moveBlock(direction.name());

        Position adjacent = Positions.add(model.getCurrentPosition(),
                direction.asPosition());

        // Update the current tile and the tile we moved the block to.
        fireBlocksChanged(model.getCurrentPosition());
        fireBlocksChanged(adjacent);
    }

    /**
     * Places block on top of the current tile.
     * @param blockType Block type to place.
     * @throws NoSuchElementException Inventory does not contain given block.
     * @throws TooHighException Tile height too high for block type.
     * @see Tile#placeBlock(Block)
     */
    @Override
    public void placeBlock(BlockType blockType)
            throws NoSuchElementException, TooHighException {
        List<Block> inventory = model.getBuilder().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (BlockType.fromBlock(inventory.get(i)) == blockType) {
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
                "No block of type "+blockType+" in the builder's inventory.");
    }
    //endregion

    /**
     * Handles an error message by dispatching an event to the model.
     * @param errorMessage Error message.
     */
    @Override
    public void handleErrorMessage(String errorMessage) {
        System.out.println("Firing error event: " + errorMessage);
        model.notifyListeners(new ErrorEvent(errorMessage));
    }

    /**
     * Handles an informational message by dispatching an event to the model.
     * @param message Message text.
     */
    @Override
    public void handleInfoMessage(String message) {
        model.notifyListeners(new MessageEvent(message));
    }
}
