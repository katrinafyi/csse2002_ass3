package game;

import csse2002.block.world.Block;
import csse2002.block.world.worldMap.getBuilder();
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
import game.model.Direction;
import game.view.BuilderControlsView;
import game.view.InventoryView;
import game.view.WorldMapView;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;


// This serves the purpose of a controller as well as dispatching events,
// because assignment 2's implementation does not do so.
public class BlockWorldController {

    private WorldMap worldMap;
    private final Map<Position, Tile> positionTileMap = new HashMap<>();

    private final WorldMapView worldMapView;
    private final BuilderControlsView builderControlsView;
    private final InventoryView inventoryView;

    public BlockWorldController(WorldMapView worldMapView,
                                BuilderControlsView builderControlsView,
                                InventoryView inventoryView) {
        this.worldMapView = worldMapView;
        this.builderControlsView = builderControlsView;
        this.inventoryView = inventoryView;
    }

    private void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;

        computePositionTileMap();

        // Inform WorldMap of new builder and tiles.
        worldMapView.newMapLoaded(worldMap.getStartPosition());
        for (Position position : positionTileMap.keySet()) {
            notifyTileHeight(position);
            notifyTopBlock(position);
            notifyTileExits(position);
        }
    }

    private void notifyTileHeight(Position position) {
        int height = positionTileMap.get(position).getBlocks().size();
        worldMapView.updateTileHeight(position, height);
    }

    private void notifyTopBlock(Position position) {
        Tile tile = positionTileMap.get(position);
        int height = tile.getBlocks().size();

        BlockType topType;
        try {
            topType = height != 0 ? BlockType.fromBlock(tile.getTopBlock()) : null;
        } catch (TooLowException e) {
            throw new AssertionError(e);
        }
        worldMapView.updateTopBlock(position, topType);
    }

    private void notifyTileExits(Position position) {
        Tile tile = positionTileMap.get(position);
        for (Direction direction : Direction.values()) {
            worldMapView.updateTileExit(
                    position,
                    direction,
                    tile.getExits().get(direction.name()) != null
            );
        }
    }

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

    public void loadWorldMapFile(String filePath)
            throws WorldMapInconsistentException, WorldMapFormatException,
            FileNotFoundException {
        setWorldMap(new WorldMap(filePath));
    }


    public void moveBuilder(Direction direction) throws NoExitException {
        Tile newTile = worldMap.getBuilder().getCurrentTile()
                .getExits().get(direction.name());
        worldMap.getBuilder().moveTo(newTile);
    }

    public void dig() throws InvalidBlockException, TooLowException {
        worldMap.getBuilder().digOnCurrentTile();
    }

    public void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException {
        worldMap.getBuilder().getCurrentTile().moveBlock(direction.name());
    }

    public void dropBlock(BlockType blockType)
            throws NoSuchElementException, TooHighException, InvalidBlockException {
        List<Block> inventory = worldMap.getBuilder().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (blockType.blockClass.isAssignableFrom(inventory.get(i).getClass())) {
                worldMap.getBuilder().dropFromInventory(i);
                return;
            }
        }
        throw new NoSuchElementException(
                "No block of type "+blockType.name()+" in worldMap.getBuilder()'s inventory.");
    }
}
