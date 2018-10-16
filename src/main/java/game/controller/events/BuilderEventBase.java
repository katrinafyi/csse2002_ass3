package game.controller.events;

import csse2002.block.world.Builder;

public class BuilderEventBase extends BaseBlockWorldEvent {
    private final Builder builder;

    public BuilderEventBase(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }
}
