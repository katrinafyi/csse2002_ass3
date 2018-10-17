package game.model;

import csse2002.block.world.Block;
import csse2002.block.world.Builder;
import csse2002.block.world.Position;
import csse2002.block.world.Tile;
import csse2002.block.world.WorldMap;
import game.Utilities;
import game.model.events.BaseBlockWorldEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GameModel extends BlockWorldModel {
    private WorldMap worldMap;
    private Position currentPosition;
    private final Map<Position, Tile> tileMap = new HashMap<>();

    @Override
    public WorldMap getWorldMap() {
        return worldMap;
    }

    @Override
    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
        setCurrentPosition(worldMap.getStartPosition());
        computePositionTileMap();
    }

    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    public Map<Position, Tile> getTileMap() {
        return Collections.unmodifiableMap(tileMap);
    }

    private void computePositionTileMap() {
        tileMap.clear();
        Set<Position> seenPositions = new HashSet<>();
        Queue<Position> positionsToSearch = new LinkedList<>();
        positionsToSearch.add(worldMap.getStartPosition());

        while (!positionsToSearch.isEmpty()) {
            Position currentPos = positionsToSearch.remove();
            if (!seenPositions.add(currentPos)) {
                continue;
            }

            Tile currentTile = worldMap.getTile(currentPos);
            tileMap.put(currentPos, currentTile);
            for (Direction dir : Direction.values()) {
                if (currentTile.getExits().get(dir.name()) != null) {
                    positionsToSearch.add(
                            Utilities.addPos(currentPos, dir.asPosition()));
                }
            }
        }
    }
}
