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
import game.model.events.WorldMapLoadedEvent;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;


// This serves the purpose of a presenter as well as dispatching events,
// because assignment 2's implementation does not do so.
public class GameController implements BlockWorldController, ErrorController {

    private final BlockWorldModel model;

    public GameController(BlockWorldModel model) {
        this.model = model;
    }

    @Override
    public void loadWorldMapFile(String filePath)
            throws WorldMapInconsistentException, WorldMapFormatException,
            FileNotFoundException {
        model.setWorldMap(new WorldMap(filePath));

        // Inform WorldMap of new builder and tiles.
        fireMapLoaded();
        for (Position position : model.getTileMap().keySet()) {
            fireBlocksChanged(position);
        }
        fireInventoryChanged();
    }

    private void fireMapLoaded() {
        model.notifyListeners(
                new WorldMapLoadedEvent(model.getCurrentPosition(), model.getTileMap()));
    }

    private void fireBlocksChanged(Position position) {
        Tile tile = model.getTileMap().get(position);
        model.notifyListeners(new BlocksChangedEvent(position, tile));
    }

    private void fireBuilderMoved(Direction dir) {
        model.notifyListeners(
                new BuilderMovedEvent(
                        model.getBuilder(), model.getCurrentPosition(),
                        model.getTileMap().get(model.getCurrentPosition()), dir));
    }

    private void fireInventoryChanged() {
        model.notifyListeners(
                new InventoryChangedEvent(model.getBuilder(), model.getInventoryCount()));
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
    public void handleError(String errorMessage) {
        System.out.println("Firing error event: " + errorMessage);
        model.notifyListeners(new ErrorEvent(errorMessage));
    }
}
