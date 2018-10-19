package game.view;

import csse2002.block.world.TooHighException;
import game.Utilities;
import game.controller.BlockWorldController;
import game.controller.MessageController;
import game.model.BlockType;
import game.model.BlockWorldModel;
import game.model.events.InventoryChangedEvent;
import game.view.components.InventoryView;
import game.view.components.TileSquare;
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

public class GameInventoryPane extends VBox implements InventoryView {
    private final static List<BlockType> carryableBlocks = new ArrayList<>();
    static {
        carryableBlocks.add(BlockType.wood);
        carryableBlocks.add(BlockType.soil);
    }

    private final GridPane grid = new GridPane();
    private final BlockWorldModel model;
    private final BlockWorldController controller;
    private final MessageController messageController;

    private final Map<BlockType, Label> countLabels = new HashMap<>();
    private final Map<BlockType, Button> blockButtons = new HashMap<>();

    public GameInventoryPane(BlockWorldModel model,
                             BlockWorldController controller,
                             MessageController messenger) {
        this.model = model;
        this.controller = controller;
        this.messageController = messenger;

        this.setId("inventory");
        grid.setId("inventory_grid");

        model.addListener(InventoryChangedEvent.class, this::updateInventory);

        applyGridLayout();

        this.getChildren().addAll(new Label("Inventory"), grid);

        generateAllBlockRows();
    }

    private void updateInventory(InventoryChangedEvent event) {
        for (Map.Entry<BlockType, Integer> blockCount : model.getInventoryCount().entrySet()) {
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
            messageController.handleErrorMessage(
                    "You can't place "+blockToPlace.name()+" up here!");
        } catch (NoSuchElementException e) {
            messageController.handleErrorMessage(
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

    @Override
    public Button getButton(BlockType blockType) {
        return blockButtons.get(blockType);
    }
}
