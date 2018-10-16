package game.controller.events;

import csse2002.block.world.Builder;

public class BuilderMovedEvent extends BuilderEvent {
    public BuilderMovedEvent(Builder builder) {
        super(builder);
    }
}
