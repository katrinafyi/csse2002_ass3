package game;

import csse2002.block.world.InvalidBlockException;
import csse2002.block.world.NoExitException;
import csse2002.block.world.Tile;
import csse2002.block.world.TooLowException;
import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldController;
import game.controller.ErrorController;
import game.model.Direction;
import game.model.EventDispatcher;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.BuilderMovedEvent;
import game.model.events.WorldMapEvent;
import game.model.events.WorldMapLoadedEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.util.Base64;
import java.util.Map;

public class GameControlsPane extends VBox {
    private final EventDispatcher<BaseBlockWorldEvent> model;
    private final BlockWorldController controller;
    private ErrorController errorController;

    private final DPadGrid dPad;

    public GameControlsPane(EventDispatcher<BaseBlockWorldEvent> model,
                            BlockWorldController controller,
                            ErrorController errorController) {
        this.model = model;
        this.controller = controller;
        this.errorController = errorController;

        model.addListener(BuilderMovedEvent.class, e -> {
            this.updateBuilderCanMove(e.getTile());
        });
        model.addListener(WorldMapLoadedEvent.class, e -> {
            this.updateBuilderCanMove(e.getTileMap().get(e.getPosition()));
        });

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);
        dPad = new DPadGrid(this::moveBuilderAndCatch);

        Button debug = new Button("(Debug)");
        debug.setOnAction((e) -> {
        });

        Button b2 = new Button("(Size to scene)");

        Button b3 = new Button("(Load map)");
        b3.setOnAction(e -> {
            try {
                controller.loadWorldMapFile("validTestCase2.txt");
            } catch (WorldMapInconsistentException | WorldMapFormatException | FileNotFoundException e1) {
                System.out.println(e1);
            }
        });

        this.getChildren().addAll(dPad, new Button("⛏") {
            {
                this.setStyle("-fx-font-size: 40; -fx-font-weight: bold;");
                setOnAction(e -> {
                    try {
                        controller.dig();
                    } catch (InvalidBlockException e1) {
                        e1.printStackTrace();
                    } catch (TooLowException e1) {
                        e1.printStackTrace();
                    }
                });
            }
        }, debug, b2, b3);
    }

    private void updateBuilderCanMove(Tile tile) {
        Map<String, Tile> exits = tile.getExits();
        for (Direction dir : Direction.values()) {
            dPad.getButton(dir).setDisable(!exits.containsKey(dir.name()));
        }
    }


    private void moveBuilderAndCatch(Direction direction) {
        try {
            controller.moveBuilder(direction);
        } catch (NoExitException e) {
            errorController.handleError("You can't move that way!");
        }
    }
}
