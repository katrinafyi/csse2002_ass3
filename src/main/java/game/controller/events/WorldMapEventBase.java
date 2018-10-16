package game.controller.events;

import csse2002.block.world.WorldMap;

public class WorldMapEventBase extends BaseBlockWorldEvent {
    private final WorldMap worldMap;

    public WorldMapEventBase(WorldMap worldMap) {

        this.worldMap = worldMap;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }
}
