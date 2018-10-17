package game;

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
import game.controller.BlockWorldController;
import game.controller.ErrorController;
import game.model.EventDispatcher;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BlocksChangedEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.InventoryChangedEvent;
import game.model.events.WorldMapLoadedEvent;
import game.model.BlockType;
import game.model.Direction;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;


// This serves the purpose of a presenter as well as dispatching events,
// because assignment 2's implementation does not do so.
public class GameAdapter extends EventDispatcher<BaseBlockWorldEvent>
        implements BlockWorldController, ErrorController {

    private WorldMap worldMap;
    private final Map<Position, Tile> positionTileMap = new HashMap<>();
    private Position currentPosition;


    @Override
    public void loadWorldMapFile(String filePath)
            throws WorldMapInconsistentException, WorldMapFormatException,
            FileNotFoundException {
        setWorldMap(new WorldMap(filePath));
    }

    private void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;

        computePositionTileMap();

        currentPosition = worldMap.getStartPosition();
        // Inform WorldMap of new builder and tiles.
        fireMapLoaded();
        for (Position position : positionTileMap.keySet()) {
            fireBlocksChanged(position);
        }
        fireInventoryChanged();
    }

    //region count inventory and generate pos-tile map.
    private Map<BlockType, Integer> countInventoryBlocks() {
        Map<BlockType, Integer> countMap = new HashMap<>();
        for (BlockType type : BlockType.values()) {
            countMap.put(type, 0);
        }
        for (Block block : worldMap.getBuilder().getInventory()) {
            BlockType type = BlockType.fromBlock(block);
            countMap.put(type, countMap.get(type) + 1);
        }
        return countMap;
    }

    private void computePositionTileMap() {
        // We need to have a copy of the position to tile mapping.
        positionTileMap.clear();
        Set<Position> seenPositions = new HashSet<>();
        Queue<Position> positionsToSearch = new LinkedList<>();
        positionsToSearch.add(worldMap.getStartPosition());
        while (!positionsToSearch.isEmpty()) {
            Position currentPos = positionsToSearch.remove();
            if (!seenPositions.add(currentPos)) {
                continue;
            }

            Tile currentTile = worldMap.getTile(currentPos);
            positionTileMap.put(currentPos, currentTile);
            for (Direction dir : Direction.values()) {
                if (currentTile.getExits().get(dir.name()) != null) {
                    positionsToSearch.add(
                            Utilities.addPos(currentPos, dir.asPosition()));
                }
            }
        }
    }

    private void fireMapLoaded() {
        notifyListeners(new WorldMapLoadedEvent(currentPosition, positionTileMap));
    }

    private void fireBlocksChanged(Position position) {
        Tile tile = positionTileMap.get(position);
        notifyListeners(new BlocksChangedEvent(position, tile));
    }

    private void fireBuilderMoved(Direction dir) {
        notifyListeners(
                new BuilderMovedEvent(
                        worldMap.getBuilder(), currentPosition,
                        positionTileMap.get(currentPosition), dir));
    }

    private void fireInventoryChanged() {
        notifyListeners(
                new InventoryChangedEvent(worldMap.getBuilder(), countInventoryBlocks()));
    }

    //region  ### Implemented world interaction functions ###
    @Override
    public void moveBuilder(Direction direction) throws NoExitException {
        Tile newTile = worldMap.getBuilder().getCurrentTile()
                .getExits().get(direction.name());
        worldMap.getBuilder().moveTo(newTile);

        currentPosition = Utilities.addPos(currentPosition,
                direction.asPosition());

        fireBuilderMoved(direction);
    }

    @Override
    public void dig() throws InvalidBlockException, TooLowException {
        worldMap.getBuilder().digOnCurrentTile();
        fireBlocksChanged(currentPosition);
    }

    @Override
    public void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException {
        worldMap.getBuilder().getCurrentTile().moveBlock(direction.name());

        // Update the current tile and the tile we moved the block to.
        Position adjacent = Utilities.addPos(currentPosition, direction.asPosition());

        fireBlocksChanged(currentPosition);
        fireBlocksChanged(adjacent);

        currentPosition = adjacent;
    }

    @Override
    public void placeBlock(BlockType blockType)
            throws NoSuchElementException, TooHighException {
        List<Block> inventory = worldMap.getBuilder().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (blockType.blockClass.isAssignableFrom(inventory.get(i).getClass())) {
                try {
                    worldMap.getBuilder().dropFromInventory(i);
                } catch (InvalidBlockException e) {
                    throw new AssertionError(e);
                }
                fireInventoryChanged();
                fireBlocksChanged(currentPosition);
                return;
            }
        }
        throw new NoSuchElementException(
                "No block of type "+blockType.name()+" in worldMap.getBuilder()'s inventory.");
    }
    //endregion

    @Override
    public void handleError(String errorMessage) {
        System.out.println("GameAdapter handleError: " + errorMessage);
    }
}
