package game.view.components;

import game.model.BlockType;
import javafx.scene.control.Button;

/**
 * Interface for views of the builder's inventory.
 */
public interface InventoryView {
    /**
     * Gets the button for placing the given block type.
     * @param blockType Block type of button to get.
     * @return Button.
     */
    Button getButton(BlockType blockType);
}
