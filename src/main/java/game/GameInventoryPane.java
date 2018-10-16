package game;

import game.model.BlockType;
import game.view.InventoryView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class GameInventoryPane extends VBox implements InventoryView {
    private final GridPane grid = new GridPane();

    public GameInventoryPane() {
        setGridLayout();

        this.getChildren().addAll(new Label("Inventory"), grid);
        generateBlockRow(0, BlockType.grass);
    }

    private void generateBlockRow(int row, BlockType blockType) {
        Button button = new Button();
        TileSquare tile = new TileSquare();
        tile.setTopBlock(blockType);
        tile.setMaxWidth(30);
        button.setGraphic(tile);
        button.setPadding(new Insets(3));
        button.prefHeightProperty().bind(button.widthProperty());
        this.grid.add(button, 0, row);
        this.grid.add(new Label(Utilities.capitalise(blockType.name())), 1, row);
        this.grid.add(new Label("×20"), 2, row);
    }

    private void setGridLayout() {
        grid.setHgap(10);

        ColumnConstraints col0 = new ColumnConstraints(30);
        ColumnConstraints col1 = new ColumnConstraints(80);
        ColumnConstraints col2 = new ColumnConstraints(30);
        col2.setHalignment(HPos.RIGHT);

        grid.getColumnConstraints().addAll(col0, col1, col2);
    }

    @Override
    public void updateInventory(Map<BlockType, Integer> blocksCount) {

    }
}
