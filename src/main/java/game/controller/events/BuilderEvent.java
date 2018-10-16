package game.controller.events;

import csse2002.block.world.Builder;

public class BuilderEvent extends BaseBlockWorldEvent {
    private final Builder builder;

    public BuilderEvent(Builder builder) {

        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }
}
