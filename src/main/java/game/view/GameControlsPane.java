package game.view;

import csse2002.block.world.Block;
import csse2002.block.world.InvalidBlockException;
import csse2002.block.world.NoExitException;
import csse2002.block.world.Tile;
import csse2002.block.world.TooHighException;
import csse2002.block.world.TooLowException;
import game.controller.BlockWorldController;
import game.controller.MessageController;
import game.model.BlockType;
import game.model.BlockWorldModel;
import game.model.Direction;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BuilderMovedEvent;
import game.view.components.ControlsView;
import game.view.components.DPadGrid;
import game.view.components.IconButton;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Pane containing two d-pads for moving the builder and blocks, as well as
 * a dig button.
 */
public class GameControlsPane extends VBox implements ControlsView {
    /** Game model the controls interact with. */
    private final BlockWorldModel model;
    /** Controller to use when this control is interacted with. */
    private final BlockWorldController controller;
    /** Controller to use to handle messages. */
    private final MessageController messenger;

    /** D-pad for moving the builder. */
    private final DPadGrid builderDPad;
    /** D-pad for moving the blocks. */
    private final DPadGrid blockDPad;
    /** Button for digging the current block. */
    private final IconButton digButton;

    /**
     * Construct a new controls pane using the given model and controllers.
     * @param model Game model to get state from.
     * @param controller Game controller to use for interacting with the world.
     * @param messenger Game controller to send messages to.
     */
    public GameControlsPane(BlockWorldModel model,
                            BlockWorldController controller,
                            MessageController messenger) {
        this.model = model;
        this.controller = controller;
        this.messenger = messenger;

        model.addListener(BuilderMovedEvent.class, this::updateBuilderExits);

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);

        builderDPad = new DPadGrid(this::moveBuilder);
        builderDPad.setCentreImage("file:src/images/steve_shadow.png");

        blockDPad = new DPadGrid(this::moveBlock);
        blockDPad.setCentreImage("file:src/images/iron_shovel_shadow.png");

        digButton = new IconButton("file:src/images/iron_pickaxe.png");
        digButton.maxWidthProperty().bind(digButton.heightProperty());
        digButton.setOnAction(e -> this.digBlock());

        this.getChildren().addAll(builderDPad, blockDPad, digButton);
    }

    /**
     * Update which exits exist from the current tile, enabling only buttons
     * which have exits.
     * @param event Event, unused.
     */
    private void updateBuilderExits(BaseBlockWorldEvent event) {
        Map<String, Tile> exits = model.getCurrentTile().getExits();
        for (Direction dir : Direction.values()) {
            boolean hasExit = exits.containsKey(dir.name());

            builderDPad.getButton(dir).setDisable(!hasExit);
            blockDPad.getButton(dir).setDisable(!hasExit);
        }
    }

    /**
     * Moves the builder in the given direction, handling exceptions from
     * too high/low by sending messages to a controller.
     * @param direction Direction the builder moved in.
     */
    private void moveBuilder(Direction direction) {
        try {
            controller.moveBuilder(direction);
        } catch (NoExitException e) {
            // Because we disable buttons when there is no exit, we know
            // this exception is caused by incorrect heights.
            int adjHeight = model.getCurrentTile()
                    .getExits().get(direction.name()).getBlocks().size();
            int ourHeight = model.getCurrentTile()
                    .getBlocks().size();
            String relation = adjHeight > ourHeight ? "high" : "low";
            messenger.handleErrorMessage("It's too "+relation+"!");
        }
    }

    /**
     * Moves a block in the given direction. Handles errors by sending
     * messages to a controller.
     * @param direction Direction to move block in.
     */
    private void moveBlock(Direction direction) {
        try {
            controller.moveBlock(direction);
        } catch (InvalidBlockException e) {
            messenger.handleErrorMessage("You can't move "+topBlockName()+"!");
        } catch (TooHighException e) {
            if (model.getCurrentTile().getBlocks().size() > 0) {
                messenger.handleErrorMessage("There's a block in the way!");
            } else {
                messenger.handleErrorMessage("You can't move bedrock!");
            }
        } catch (NoExitException ignored) {}
    }

    /**
     * Attempts to dig on the current tile.
     */
    private void digBlock() {
        try {
            controller.dig();
        } catch (InvalidBlockException | TooLowException e) {
            messenger.handleErrorMessage(
                    "You can't dig "+topBlockName()+"!");
        }
    }

    /**
     * Gets name of the current tile's top block. If there are no blocks,
     * "bedrock" is returned.
     * @return Name as string.
     */
    private String topBlockName() {
        Tile tile = model.getCurrentTile();
        Block block = null;
        if (tile.getBlocks().size() > 0) {
            try {
                block = tile.getTopBlock();
            } catch (TooLowException ignored) {}
        }
        return block == null ? "bedrock" : BlockType.fromBlock(block).name();
    }

    /**
     * Gets the button for moving the builder in the given direction.
     * @param direction Direction to get.
     * @return Direction button.
     */
    @Override
    public Button getMoveBuilderButton(Direction direction) {
        return builderDPad.getButton(direction);
    }

    /**
     * Gets the button for moving a block in the given direction.
     * @param direction Direction to get.
     * @return Move block button.
     */
    @Override
    public Button getMoveBlockButton(Direction direction) {
        return blockDPad.getButton(direction);
    }

    /**
     * Gets the button for digging.
     * @return Dig button.
     */
    @Override
    public Button getDigButton() {
        return digButton;
    }
}
