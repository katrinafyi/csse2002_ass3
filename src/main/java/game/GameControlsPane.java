package game;

import csse2002.block.world.NoExitException;
import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldController;
import game.controller.ErrorController;
import game.model.Direction;
import game.view.BuilderControlsView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;

public class GameControlsPane extends VBox implements BuilderControlsView {
    private final BlockWorldController controller;
    private ErrorController errorController;

    public GameControlsPane(BlockWorldController controller, ErrorController errorController) {
        this.controller = controller;
        this.errorController = errorController;

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);
        DPadGrid grid = new DPadGrid(this::moveBuilderAndCatch);

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

        this.getChildren().addAll(grid, new Button("⛏") {
            {
                this.setStyle("-fx-font-size: 40; -fx-font-weight: bold;");
            }
        }, debug, b2, b3);
    }

    private void moveBuilderAndCatch(Direction direction) {
        try {
            controller.moveBuilder(direction);
        } catch (NoExitException e) {
            errorController.handleError("You can't move that way!");
        }
    }
    
    @Override
    public void updateCanMoveBuilder(Direction direction, boolean canMove) {

    }

    @Override
    public void updateCanDig(boolean canDig) {

    }
}
