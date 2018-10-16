package game.model.events;

import csse2002.block.world.Builder;

/**
 * Superclass for events relating to the builder.
 */
public abstract class BuilderEvent extends BaseBlockWorldEvent {
    private final Builder builder;

    public BuilderEvent(Builder builder) {
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }
}
