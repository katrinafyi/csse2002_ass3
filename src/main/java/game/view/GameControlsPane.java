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
import game.model.events.WorldMapLoadedEvent;
import game.util.Utilities;
import game.view.components.ControlsView;
import game.view.components.DPadGrid;
import game.view.components.IconButton;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class GameControlsPane extends VBox implements ControlsView {
    private final BlockWorldModel model;
    private final BlockWorldController controller;
    private final MessageController messageController;

    private final DPadGrid builderDPad;
    private final DPadGrid blockDPad;
    private final IconButton digButton;

    public GameControlsPane(BlockWorldModel model,
                            BlockWorldController controller,
                            MessageController messenger) {
        this.model = model;
        this.controller = controller;
        this.messageController = messenger;

        model.addListener(BuilderMovedEvent.class, this::updateBuilderCanMove);

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);

        builderDPad = new DPadGrid(this::moveBuilder);
        builderDPad.setCentreImage("file:src/images/steve_shadow.png");

        blockDPad = new DPadGrid(this::moveBlock);
        blockDPad.setCentreImage("file:src/images/iron_shovel_shadow.png");

        digButton = new IconButton("file:src/images/iron_pickaxe.png");
        Utilities.setMaxWidthHeight(digButton);
        digButton.maxWidthProperty().bind(digButton.heightProperty());
        digButton.setOnAction(e -> this.digBlock());

        this.getChildren().addAll(builderDPad, blockDPad, digButton);
    }

    private void updateBuilderCanMove(BaseBlockWorldEvent event) {
        Map<String, Tile> exits = model.getCurrentTile().getExits();
        for (Direction dir : Direction.values()) {
            boolean hasExit = exits.containsKey(dir.name());

            builderDPad.getButton(dir).setDisable(!hasExit);
            blockDPad.getButton(dir).setDisable(!hasExit);
        }
    }


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
            messageController.handleErrorMessage("It's too "+relation+"!");
        }
    }

    private void moveBlock(Direction direction) {
        try {
            controller.moveBlock(direction);
        } catch (NoExitException | InvalidBlockException | TooHighException e) {
            List<Block> blocks = model.getCurrentTile().getBlocks();
            String message;
            if (blocks.size() == 0) {
                message = "You can't move bedrock!";
            } else {
                Block topBlock = blocks.get(blocks.size()-1);
                if (!topBlock.isMoveable()) {
                    message = "You can't move " + BlockType.fromBlock(topBlock) + "!";
                } else {
                    message = "There's a block in the way!";
                }
            }
            messageController.handleErrorMessage(message);
        }
    }

    private void digBlock() {
        try {
            controller.dig();
        } catch (InvalidBlockException | TooLowException e) {
            messageController.handleErrorMessage(
                    "You can't dig "+topBlockName()+"!");
        }
    }

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

    @Override
    public Button getMoveBuilderButton(Direction direction) {
        return builderDPad.getButton(direction);
    }

    @Override
    public Button getMoveBlockButton(Direction direction) {
        return blockDPad.getButton(direction);
    }

    @Override
    public Button getDigButton() {
        return digButton;
    }
}
