package game.view;

import csse2002.block.world.TooHighException;
import game.view.components.TileSquare;
import game.Utilities;
import game.controller.BlockWorldController;
import game.controller.ErrorController;
import game.model.BlockType;
import game.model.EventDispatcher;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.InventoryChangedEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class GameInventoryPane extends VBox {
    private final static List<BlockType> carryableBlocks = new ArrayList<>();
    static {
        carryableBlocks.add(BlockType.wood);
        carryableBlocks.add(BlockType.soil);
    }

    private final GridPane grid = new GridPane();
    private final EventDispatcher<BaseBlockWorldEvent> model;
    private final BlockWorldController controller;
    private final ErrorController errorController;

    private final Map<BlockType, Label> countLabels = new HashMap<>();
    private final Map<BlockType, Button> blockButtons = new HashMap<>();

    public GameInventoryPane(EventDispatcher<BaseBlockWorldEvent> model,
                             BlockWorldController controller,
                             ErrorController errorController) {
        this.model = model;
        this.controller = controller;
        this.errorController = errorController;

        this.setId("inventory");
        grid.setId("inventory_grid");

        model.addListener(InventoryChangedEvent.class, this::updateInventory);

        applyGridLayout();

        this.getChildren().addAll(new Label("Inventory"), grid);

        generateAllBlockRows();
    }

    private void updateInventory(InventoryChangedEvent event) {
        for (Map.Entry<BlockType, Integer> blockCount : event.getBlocksCount().entrySet()) {
            BlockType block = blockCount.getKey();
            if (!carryableBlocks.contains(block)) {
                continue;
            }
            int n = blockCount.getValue();
            countLabels.get(block).setText("×"+n);
        }
    }

    private void handlePlaceBlock(BlockType blockToPlace) {
        try {
            controller.placeBlock(blockToPlace);
        } catch (TooHighException e) {
            errorController.handleError(
                    "You can't place "+blockToPlace.name()+" up here!");
        } catch (NoSuchElementException e) {
            errorController.handleError(
                    "You have no more "+blockToPlace.name()+"!");
        }
    }

    private void generateAllBlockRows() {
        int i = 0;
        for (BlockType blockType : carryableBlocks) {
            generateBlockRow(i, blockType);
            i++;
        }
    }

    private void generateBlockRow(int row, BlockType blockType) {
        Button button = new Button();
        TileSquare tile = new TileSquare();
        tile.setTopBlock(blockType);
        tile.setMaxWidth(40);

        button.setOnAction(e -> handlePlaceBlock(blockType));
        button.setDisable(true);
        button.setGraphic(tile);
        button.setPadding(new Insets(3));
        button.prefHeightProperty().bind(button.widthProperty());

        this.grid.add(button, 0, row);
        blockButtons.put(blockType, button);

        this.grid.add(createBlockLabel(blockType), 1, row);

        Label count = createCountLabel();
        this.grid.add(count, 2, row);
        countLabels.put(blockType, count);
    }

    private static Label createBlockLabel(BlockType blockType) {
        Label label = new Label(Utilities.capitalise(blockType.name()));
        label.getStyleClass().add("block_type");
        label.setStyle("-fx-font-size: 18;");
        return label;
    }

    private static Label createCountLabel() {
        Label label = new Label("–");
        label.setStyle("-fx-font-size: 18;");
        return label;
    }

    private void applyGridLayout() {
        grid.setHgap(15);
        grid.setVgap(5);

        ColumnConstraints col0 = new ColumnConstraints(40);
        ColumnConstraints col1 = new ColumnConstraints(50);
        ColumnConstraints col2 = new ColumnConstraints(30);
        col2.setHalignment(HPos.RIGHT);

        grid.getColumnConstraints().addAll(col0, col1, col2);
    }
}
