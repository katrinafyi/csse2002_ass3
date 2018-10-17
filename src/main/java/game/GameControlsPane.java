package game;

import csse2002.block.world.InvalidBlockException;
import csse2002.block.world.NoExitException;
import csse2002.block.world.Tile;
import csse2002.block.world.TooHighException;
import csse2002.block.world.TooLowException;
import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldController;
import game.controller.ErrorController;
import game.model.Direction;
import game.model.EventDispatcher;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.WorldMapLoadedEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.util.Map;

public class GameControlsPane extends VBox {
    private final EventDispatcher<BaseBlockWorldEvent> model;
    private final BlockWorldController controller;
    private ErrorController errorController;

    private final DPadGrid builderDPad;
    private final DPadGrid blockDPad;
    private final IconButton digButton;

    public GameControlsPane(EventDispatcher<BaseBlockWorldEvent> model,
                            BlockWorldController controller,
                            ErrorController errorController) {
        this.model = model;
        this.controller = controller;
        this.errorController = errorController;

        model.addListener(BuilderMovedEvent.class, e -> {
            this.updateBuilderCanMove(e.getTile());
        });
        model.addListener(WorldMapLoadedEvent.class,
                this::worldMapLoadedListener);

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);

        builderDPad = new DPadGrid(this::moveBuilder);
        builderDPad.setCentreImage("file:src/images/steve_shadow.png");

        blockDPad = new DPadGrid(this::moveBlock);
        blockDPad.setCentreImage("file:src/images/iron_shovel_shadow.png");

        digButton = new IconButton("file:src/images/iron_pickaxe.png");
        digButton.setDisable(true);
        digButton.maxWidthProperty().bind(digButton.heightProperty());
        digButton.setOnAction(e -> this.digBlock());

        Button b3 = new Button("(Load map)");
        b3.setOnAction(e -> {
            try {
                controller.loadWorldMapFile("validTestCase2.txt");
            } catch (WorldMapInconsistentException | WorldMapFormatException | FileNotFoundException e1) {
                System.out.println(e1);
            }
        });

        this.getChildren().addAll(builderDPad, blockDPad, digButton, b3);
    }

    private void worldMapLoadedListener(WorldMapLoadedEvent event) {
        this.updateBuilderCanMove(event.getTileMap().get(event.getPosition()));

        // Enable all buttons.
        builderDPad.getCentreImage().setOpacity(1);
        blockDPad.getCentreImage().setOpacity(1);
        digButton.setDisable(false);
    }

    private void updateBuilderCanMove(Tile tile) {
        Map<String, Tile> exits = tile.getExits();
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
            errorController.handleError("You can't move that way!");
        }
    }

    private void moveBlock(Direction direction) {
        try {
            controller.moveBlock(direction);
        } catch (NoExitException | InvalidBlockException | TooHighException e) {
            errorController.handleError("You can't move this block there!");
        }
    }

    private void digBlock() {
        try {
            controller.dig();
        } catch (InvalidBlockException | TooLowException e) {
            errorController.handleError("You can't dig there!");
        }
    }
}
