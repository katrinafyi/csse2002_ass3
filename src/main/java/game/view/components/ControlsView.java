package game.view.components;

import game.model.Direction;
import javafx.scene.control.Button;

public interface ControlsView {
    Button getMoveBuilderButton(Direction direction);
    Button getMoveBlockButton(Direction direction);
    Button getDigButton();
}
