package game.view.components;

import game.model.Direction;
import javafx.scene.control.Button;

/**
 * Interface for views of the builder's controls.
 */
public interface ControlsView {
    /**
     * Returns the button for moving the builder in the given direction.
     * @param direction Direction to get.
     * @return Button.
     */
    Button getMoveBuilderButton(Direction direction);

    /**
     * Returns the button for moving a block in the given direction.
     * @param direction Direction to get.
     * @return Button.
     */
    Button getMoveBlockButton(Direction direction);

    /**
     * Returns the button for digging the current block.
     * @return Button.
     */
    Button getDigButton();
}
