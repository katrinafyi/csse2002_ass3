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
import game.controller.BlockWorldActions;
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


// This serves the purpose of a presenter as well as dispatching events,
// because assignment 2's implementation does not do so.
public class GamePresenter implements BlockWorldActions {

    private WorldMap worldMap;
    private final Map<Position, Tile> positionTileMap = new HashMap<>();
    private Position currentPosition;

    private WorldMapView worldMapView;
    private BuilderControlsView builderControlsView;
    private InventoryView inventoryView;

    public void setWorldMapView(WorldMapView worldMapView) {
        this.worldMapView = worldMapView;
    }

    public void setBuilderControlsView(BuilderControlsView builderControlsView) {
        this.builderControlsView = builderControlsView;
    }

    public void setInventoryView(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
    }

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
        worldMapView.newMapLoaded(currentPosition);
        for (Position position : positionTileMap.keySet()) {
            notifyTileHeight(position);
            notifyTopBlock(position);
            notifyTileExits(position);
        }

        // Update other UI components with state.
        notifyCanDig();
        notifyCanMoveBuilder();
        notifyInventory();
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
    //endregion

    //region notify* methods for WorldMapView
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

    private void notifyBuilderMove(Direction dir) {
        worldMapView.moveBuilder(dir, currentPosition);
    }
    //endregion

    //region notify* for BuilderControlsView and InventoryView
    private void notifyCanMoveBuilder() {
        for (Direction dir : Direction.values()) {
            Tile adjTile = worldMap.getBuilder().getCurrentTile()
                    .getExits().get(dir.name());

            builderControlsView.updateCanMoveBuilder(
                    dir,
                    adjTile != null && worldMap.getBuilder().canEnter(adjTile)
            );
        }
    }

    private void notifyCanDig() {
        boolean canDig = false;
        try {
            canDig = worldMap.getBuilder().getCurrentTile().getTopBlock().isDiggable();
        } catch (TooLowException ignored) {} // No blocks. Can't dig.
        builderControlsView.updateCanDig(canDig);
    }

    private void notifyInventory() {
        inventoryView.updateInventory(countInventoryBlocks());
    }
    //endregion

    /**
     * Notifies all relevant observers when the blocks on the tile at this
     * position changes.
     * @param position Position of tile.
     */
    private void tileBlocksChanged(Position position) {
        if (position.equals(currentPosition)) {
            // If a block is added or removed, the height will change
            // which will change these.
            notifyCanDig();
            notifyCanMoveBuilder();
        }

        notifyTopBlock(position);
        notifyTileHeight(position);
    }


    //region  ### Implemented world interaction functions ###
    @Override
    public void moveBuilder(Direction direction) throws NoExitException {
        Tile newTile = worldMap.getBuilder().getCurrentTile()
                .getExits().get(direction.name());
        worldMap.getBuilder().moveTo(newTile);

        currentPosition = Utilities.addPos(currentPosition,
                direction.asPosition());

        notifyBuilderMove(direction);
        notifyCanMoveBuilder();
        notifyCanDig();
    }

    @Override
    public void dig() throws InvalidBlockException, TooLowException {
        worldMap.getBuilder().digOnCurrentTile();

        tileBlocksChanged(currentPosition);
    }

    @Override
    public void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException {
        worldMap.getBuilder().getCurrentTile().moveBlock(direction.name());

        // Update the current tile and the tile we moved the block to.
        Position adjacent = Utilities.addPos(currentPosition, direction.asPosition());

        tileBlocksChanged(currentPosition);
        tileBlocksChanged(adjacent);
    }

    @Override
    public void dropBlock(BlockType blockType)
            throws NoSuchElementException, TooHighException, InvalidBlockException {
        List<Block> inventory = worldMap.getBuilder().getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (blockType.blockClass.isAssignableFrom(inventory.get(i).getClass())) {
                worldMap.getBuilder().dropFromInventory(i);

                tileBlocksChanged(currentPosition);
                return;
            }
        }
        throw new NoSuchElementException(
                "No block of type "+blockType.name()+" in worldMap.getBuilder()'s inventory.");
    }

    //endregion
}
