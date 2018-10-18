package game.view.components;

import game.model.BlockType;
import javafx.scene.control.Button;

public interface InventoryView {
    Button getButton(BlockType blockType);
}
