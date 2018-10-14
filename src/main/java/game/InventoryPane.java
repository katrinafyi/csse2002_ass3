package game;

import game.model.BlockType;
import game.view.InventoryView;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class InventoryPane extends VBox implements InventoryView {
    public InventoryPane() {
        this.getChildren().add(new Label("Inventory"));
    }

    @Override
    public void updateInventory(Map<BlockType, Integer> blocksCount) {

    }
}
