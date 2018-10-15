package game;

import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldActions;
import game.model.Direction;
import game.view.BuilderControlsView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;

public class BuilderControlsPane extends VBox implements BuilderControlsView {
    private final BlockWorldActions controller;

    public BuilderControlsPane(BlockWorldActions controller) {
        this.controller = controller;
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);
        DPadGrid grid = new DPadGrid();

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

        this.getChildren().addAll(grid, new Button("DIG"), debug, b2, b3);
    }
    
    @Override
    public void updateCanMoveBuilder(Direction direction, boolean canMove) {

    }

    @Override
    public void updateCanDig(boolean canDig) {

    }
}
