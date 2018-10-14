package game;

import csse2002.block.world.Block;
import csse2002.block.world.Builder;
import csse2002.block.world.InvalidBlockException;
import csse2002.block.world.NoExitException;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.TooHighException;
import csse2002.block.world.TooLowException;
import csse2002.block.world.WorldMap;
import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Queue;
import java.util.Set;


// This serves the purpose of a controller as well as dispatching events,
// because assignment 2's implementation does not do so.
public class BlockWorldController {

    private WorldMap worldMap;
    private Builder builder;
    private final Map<Position, Tile> positionTileMap = new HashMap<>();

    public BlockWorldController() {}

    private void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
        this.builder = worldMap.getBuilder();

        createPositionTileMapping();
    }

    public void loadWorldMapFile(String filePath)
            throws WorldMapInconsistentException, WorldMapFormatException,
            FileNotFoundException {
        setWorldMap(new WorldMap(filePath));
    }

    public WorldMap getWorldMap() {
        return this.worldMap;
    }

    public Map<Position, Tile> getPositionTileMap() {
        return Collections.unmodifiableMap(positionTileMap);
    }

    private void createPositionTileMapping() {
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

    public void move(Direction direction) throws NoExitException {
        Tile newTile = builder.getCurrentTile()
                .getExits().get(direction.name());
        builder.moveTo(newTile);


    }

    public void dig() throws InvalidBlockException, TooLowException {
        builder.digOnCurrentTile();
    }

    public void moveBlock(Direction direction)
            throws NoExitException, InvalidBlockException, TooHighException {
        builder.getCurrentTile().moveBlock(direction.name());
    }

    public int countBlocks(Class<? extends Block> type) {
        int count = 0;
        for (Block block : builder.getInventory()) {
            if (type.isAssignableFrom(block.getClass())) {
                count++;
            }
        }
        return count;
    }

    public void dropBlock(Class<? extends Block> blockType)
            throws NoSuchElementException, TooHighException, InvalidBlockException {
        List<Block> inventory = builder.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (blockType.isAssignableFrom(inventory.get(i).getClass())) {
                builder.dropFromInventory(i);
                return;
            }
        }
        throw new NoSuchElementException(
                "No block of type "+type.name()+" in builder's inventory.");
    }
}
