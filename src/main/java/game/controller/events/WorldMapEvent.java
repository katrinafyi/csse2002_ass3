package game.controller.events;

import csse2002.block.world.WorldMap;

public class WorldMapEvent extends BaseBlockWorldEvent {
    private final WorldMap worldMap;

    public WorldMapEvent(WorldMap worldMap) {

        this.worldMap = worldMap;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }
}
