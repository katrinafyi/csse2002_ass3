package game;

import game.controller.BlockWorldController;
import game.model.Direction;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DPadGrid extends UniformGridPane {
    private static class ButtonSpec {
        public final String label;
        public final int col;
        public final int row;
        public final Direction direction;

        public ButtonSpec(String label, int col, int row, Direction direction) {
            this.label = label;
            this.col = col;
            this.row = row;
            this.direction = direction;
        }
    }

    private static final ButtonSpec[] buttonSpecs = new ButtonSpec[4];
    static {
        buttonSpecs[0] = new ButtonSpec("↑", 1, 0, Direction.north);
        buttonSpecs[1] = new ButtonSpec("→", 2, 1, Direction.east);
        buttonSpecs[2] = new ButtonSpec("↓", 1, 2, Direction.south);
        buttonSpecs[3] = new ButtonSpec("←", 0, 1, Direction.west);
    }

    private final Map<Direction, Button> buttons = new HashMap<>();

    public DPadGrid(Consumer<Direction> moveBuilder) {
        super(3, 3);
        fixHeightToWidth();

        for (ButtonSpec buttonSpec : buttonSpecs) {
            Button button = new Button(buttonSpec.label);
            button.setOnAction(e -> {
                moveBuilder.accept(buttonSpec.direction);
            });
            Utilities.setMaxWidthHeight(button);
            buttons.put(buttonSpec.direction, button);
            button.setDisable(true);
            this.add(button, buttonSpec.col, buttonSpec.row);
        }
    }

    public Button getButton(Direction direction) {
        return buttons.get(direction);
    }
}
