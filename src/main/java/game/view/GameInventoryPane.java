package game.view;

import csse2002.block.world.TooHighException;
import game.util.Utilities;
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

/**
 * View of the builder's inventory. Made up of two buttons for placing wood
 * and soil blocks, as well as the count of each block type.
 */
public class GameInventoryPane extends VBox implements InventoryView {
    /** Blocks to add to the inventory view. */
    private final static List<BlockType> carryableBlocks = new ArrayList<>();
    static {
        carryableBlocks.add(BlockType.wood);
        carryableBlocks.add(BlockType.soil);
    }

    /** Grid containing the block type buttons and counts. */
    private final GridPane grid = new GridPane();
    /** Model to get block counts from. */
    private final BlockWorldModel model;
    /** Controller to use for placing blocks. */
    private final BlockWorldController controller;
    /** Controller to use for displaying messages. */
    private final MessageController messageController;

    /** Labels containing the count of each block type. */
    private final Map<BlockType, Label> countLabels = new HashMap<>();
    /** Buttons for placing each block type. */
    private final Map<BlockType, Button> blockButtons = new HashMap<>();

    /**
     * Constructs a new inventory pane, using data from the given model and
     * interacting with the given controllers.
     * @param model Game model.
     * @param controller Game controller.
     * @param messenger Message controller.
     */
    public GameInventoryPane(BlockWorldModel model,
                             BlockWorldController controller,
                             MessageController messenger) {
        this.model = model;
        this.controller = controller;
        this.messageController = messenger;

        model.addListener(InventoryChangedEvent.class, this::updateInventory);

        applyGridLayout();
        this.getChildren().add(grid);

        generateAllBlockRows();
    }

    /**
     * Updates the block counts of all blocks, getting data from the model.
     * @param event Event.
     */
    private void updateInventory(InventoryChangedEvent event) {
        for (Map.Entry<BlockType, Integer> blockCount : model.getInventoryCount().entrySet()) {
            BlockType block = blockCount.getKey();
            if (!carryableBlocks.contains(block)) {
                // Ignore non-carryable blocks. These shouldn't be in the
                // inventory anyway.
                continue;
            }
            int n = blockCount.getValue();
            countLabels.get(block).setText("×"+n);
        }
    }

    /**
     * Places a block of the given type on the builder's tile.
     * @param blockToPlace Block type to place.
     */
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

    /**
     * Generates buttons and labels for all carryable blocks.
     */
    private void generateAllBlockRows() {
        int i = 0;
        for (BlockType blockType : carryableBlocks) {
            generateBlockRow(i, blockType);
            i++;
        }
    }

    /**
     * Generates and inserts a row (containing button, block type name and
     * count) for a given block type at the given grid row.
     * @param row Index of grid row.
     * @param blockType Block type of this row.
     */
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

        Label blockName = createLabel(Utilities.capitalise(blockType.name()));
        this.grid.add(blockName, 1, row);

        Label count = createLabel("–");
        this.grid.add(count, 2, row);
        countLabels.put(blockType, count);
    }

    /**
     * Creates and returns a label containing the given text.
     * @param text Text of label.
     * @return Label object.
     */
    private static Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15;");
        return label;
    }

    /**
     * Applies the appropriate layout to the grid. This includes margins and
     * padding, column widths and alignment.
     */
    private void applyGridLayout() {
        grid.setHgap(15);
        grid.setVgap(5);

        ColumnConstraints col0 = new ColumnConstraints(40);
        ColumnConstraints col1 = new ColumnConstraints(43);
        ColumnConstraints col2 = new ColumnConstraints(37);
        col2.setHalignment(HPos.RIGHT);

        grid.getColumnConstraints().addAll(col0, col1, col2);
    }

    /**
     * Returns the button for placing the given block.
     * @param blockType Block type of button to get.
     * @return Button which places the block type.
     */
    @Override
    public Button getButton(BlockType blockType) {
        return blockButtons.get(blockType);
    }
}
