package game;

import game.controller.GameController;
import game.controller.MessageController;
import game.model.BlockType;
import game.model.Direction;
import game.model.GameModel;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.WorldMapLoadedEvent;
import game.util.Utilities;
import game.view.GameControlsPane;
import game.view.GameInventoryPane;
import game.view.GameMenuBar;
import game.view.GameWorldMapView;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Entry point of the block world game. Initialises the GUI and
 * sets up the MVC structure.
 */
public class MainApplication extends Application {

    /** Primary stage. */
    private Stage primaryStage;
    /** Model component of the game. */
    private GameModel model;
    /** Controller component of the game. */
    private GameController controller;

    /** Top menu bar. */
    private GameMenuBar menuBar;
    /** 9x9 view of the world map. */
    private GameWorldMapView worldMapView;
    /** VBox containing the world map view. */
    private VBox worldMapContainer;
    /** Builder controls pane. */
    private GameControlsPane controlsPane;
    /** Inventory pane. */
    private GameInventoryPane inventoryPane;
    /** VBox containing all elements on the right of the world map view. */
    private VBox rightBox;

    /** Scene of the primary stage. */
    private Scene scene;

    /**
     * Mapping of key code to the button which should be clicked when that
     * key is pressed.
     */
    private final Map<KeyCode, Button> keyBindings = new HashMap<>();

    /** Horizontal gap between grid columns. */
    private final double hGap = 10;
    /** Padding around the entire grid. */
    private final double gridPadding = 10;

    /** Whether layout debugging is currently enabled (bounds shaded). */
    private boolean debugEnabled = false;

    /**
     * Start the game application.
     * @param primaryStage Primary stage.
     */
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // This grid contains everything except the menu bar.
        GridPane mainGrid = new GridPane();
        VBox.setVgrow(mainGrid, Priority.ALWAYS);
        mainGrid.setPadding(new Insets(gridPadding));
        mainGrid.setHgap(hGap);

        model = new GameModel();
        controller = new GameController(model);

        model.addListener(WorldMapLoadedEvent.class, this::activateControls);

        // Just in case later, we separate this out from the main controller.
        MessageController messenger = controller;

        menuBar = new GameMenuBar(primaryStage, model, controller, messenger);

        // Root container contains the main grid as well as the menu bar.
        VBox rootContainer = new VBox();
        rootContainer.getChildren().addAll(menuBar, mainGrid);

        // Container for the world map view. The container grows to all the
        // available space, then the map view itself only expands as big as it
        // can while staying square.
        worldMapContainer = new VBox();
        Utilities.setMaxWidthHeight(worldMapContainer);
        worldMapContainer.setAlignment(Pos.TOP_CENTER);
        mainGrid.add(worldMapContainer, 0, 0);

        worldMapView = new GameWorldMapView(model);
        Utilities.setBorder(worldMapView, Color.BLACK);
        worldMapContainer.getChildren().add(worldMapView);

        // Contains all elements right of the world map view.
        rightBox = new VBox();
        rightBox.setSpacing(30);
        rightBox.setAlignment(Pos.TOP_CENTER);

        // Builder controls view. Disable by default.
        controlsPane = new GameControlsPane(model, controller, controller);
        controlsPane.setDisable(true);
        // Inventory view.
        inventoryPane = new GameInventoryPane(model, controller, controller);
        inventoryPane.setDisable(true);

        // Add controls to right box and add right box to grid.
        rightBox.getChildren().addAll(controlsPane, inventoryPane);
        mainGrid.add(rightBox, 1, 0);

        // Column dimensions.
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(150);
        col1.setMinWidth(150);
        mainGrid.getColumnConstraints().addAll(col0, col1);
        // Row expands as space becomes available.
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(100);
        mainGrid.getRowConstraints().add(row0);

        // Construct and apply a scene with the root container.
        scene = new Scene(rootContainer);
        primaryStage.setScene(scene);

        // Resize world map view by listening to width and height changes.
        // The delayBinding only resizes after no size changes have occurred
        // in 200ms, as resizing requires a full redraw of the world map.
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
        Utilities.<Number>delayBinding(pauseTransition,
                primaryStage.widthProperty(), this::setWorldMapViewDimensions);
        Utilities.<Number>delayBinding(pauseTransition,
                primaryStage.heightProperty(), this::setWorldMapViewDimensions);

        // Helper method to populate the key bindings mapping.
        setKeyBindings();
        // Listen to key presses.
        scene.setOnKeyPressed(this::keyPressHandler);

        // Main window title.
        primaryStage.setTitle("DigDrop");

        // Add application icons of multiple sizes.
        for (int i = 0; i < 4; i++) {
            primaryStage.getIcons().add(new Image("file:src/images/icon"+i+".png"));
        }
        // Set starting height of window.
        primaryStage.setHeight(585);
        // Set starting height of world map view. Must be square and divisible
        // by 9.
        worldMapView.setPrefWidth(495);
        worldMapView.setPrefHeight(495);

        primaryStage.show();

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(585);
    }

    /**
     * Activates the builder and inventory controls.
     * @param e Event.
     */
    @SuppressWarnings("unused")
    private void activateControls(BaseBlockWorldEvent e) {
        controlsPane.setDisable(false);
        inventoryPane.setDisable(false);
    }

    /**
     * Updates the world map dimensions based on the window dimensions.
     * @param observable Observable property.
     * @param oldValue Old value of property.
     * @param newValue New value of property.
     */
    @SuppressWarnings("unused")
    private void setWorldMapViewDimensions(ObservableValue<? extends Number> observable,
                                           Number oldValue, Number newValue) {
        int cols = worldMapView.columns;
        // Constrain to the smallest limiting size of the following:
        // Container sizes.
        double size = Math.min(worldMapContainer.getWidth(),
                worldMapContainer.getHeight());
        // Width of window.
        size = Math.min(size, scene.getWidth()-180);
        size = Math.min(size, scene.getHeight()-45.5);
        size = Math.max(size, cols);
        // Round down to nearest multiple of number of columns for
        // seamless edges.
        size = cols*Math.floor(size/cols);

        // Set width and height to this size, making it square.
        worldMapView.setPrefHeight(size);
        worldMapView.setPrefWidth(size);
    }

    /**
     * Key press handler.
     * @param keyEvent Key event.
     */
    private void keyPressHandler(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.S && keyEvent.isControlDown()) {
            return; // Used by save shortcut. Do nothing.
        }

        // Get and click the equivalent button.
        Button actionButton = keyBindings.get(keyEvent.getCode());
        if (actionButton != null) {
            actionButton.fire();
        }

        // Extra key bindings which don't have an associated button.
        switch (keyEvent.getCode()) {
            case Z:
                worldMapView.setHeightsVisible(!worldMapView.isHeightsVisible());
                break;
            case X:
                worldMapView.setExitsVisible(!worldMapView.isExitsVisible());
                break;
            case C:
                worldMapView.setAmbientOcclusionOn(
                        !worldMapView.isAmbientOcclusionOn());
                break;
            // Debug keys for showing layout information.
            /*
            case OPEN_BRACKET: // Print layout info.
                debugPrintSize(worldMapView);
                debugPrintSize(worldMapContainer);
                debugPrintSize(scene);
                debugPrintSize(primaryStage);
                System.out.println();
                debugPrintSize(controlsPane);
                debugPrintSize(inventoryPane);
                System.out.println();
                System.out.println(worldMapView.getChildren());
                debugPrintClasses(worldMapView.getChildren());
                break;
            case CLOSE_BRACKET:
                toggleDebugLayout();
                break;
            */
            // These don't work with the provided WorldMap implementation.
            /*
            case J: // Creative mode hacks for building maps.
                model.getCurrentTile().getBlocks().add(new StoneBlock());
                model.notifyListeners(new BlocksChangedEvent(model.getCurrentPosition()));
                break;
            case K:
                model.getCurrentTile().getBlocks().add(new GrassBlock());
                model.notifyListeners(new BlocksChangedEvent(model.getCurrentPosition()));
                break;
            case L:
                int size = model.getCurrentTile().getBlocks().size();
                model.getCurrentTile().getBlocks().remove(size-1);
                model.notifyListeners(new BlocksChangedEvent(model.getCurrentPosition()));
                break;
            */
        }
    }

    /**
     * Sets all the keys which are handled by simulating a button click.
     */
    private void setKeyBindings() {
        // Move builder buttons.
        keyBindings.put(KeyCode.W,
                controlsPane.getMoveBuilderButton(Direction.north));
        keyBindings.put(KeyCode.D,
                controlsPane.getMoveBuilderButton(Direction.east));
        keyBindings.put(KeyCode.S,
                controlsPane.getMoveBuilderButton(Direction.south));
        keyBindings.put(KeyCode.A,
                controlsPane.getMoveBuilderButton(Direction.west));
        // Move block.
        keyBindings.put(KeyCode.UP,
                controlsPane.getMoveBlockButton(Direction.north));
        keyBindings.put(KeyCode.RIGHT,
                controlsPane.getMoveBlockButton(Direction.east));
        keyBindings.put(KeyCode.DOWN,
                controlsPane.getMoveBlockButton(Direction.south));
        keyBindings.put(KeyCode.LEFT,
                controlsPane.getMoveBlockButton(Direction.west));
        // Dig button.
        keyBindings.put(KeyCode.Q, controlsPane.getDigButton());
        // Place blocks.
        keyBindings.put(KeyCode.DIGIT1, inventoryPane.getButton(BlockType.wood));
        keyBindings.put(KeyCode.DIGIT2, inventoryPane.getButton(BlockType.soil));
    }

    /**
     * Toggles highlighting backgrounds of elements.
     */
    private void toggleDebugLayout() {
        debugEnabled = !debugEnabled;
        if (debugEnabled) {
            Utilities.setBackground(worldMapContainer, Color.PURPLE);
            Utilities.setBackground(controlsPane, Color.GREEN);
            Utilities.setBackground(inventoryPane, Color.YELLOW);
            Utilities.setBackground(rightBox, Color.RED);
        } else {
            Utilities.setBackground(worldMapContainer, null);
            Utilities.setBackground(controlsPane, null);
            Utilities.setBackground(inventoryPane, null);
            Utilities.setBackground(rightBox, null);
        }
    }

    /**
     * Prints the given node and its width and height.
     * @param node Node with getWidth() and getHeight().
     */
    private void debugPrintSize(Object node) {
        try {
            // This is really bad practice but Node, Scene and Region have no
            // common superclass exposing these methods, but they are all
            // present. Otherwise, we would need 3 almost identical methods.
            String w = node.getClass().getMethod("getWidth").invoke(node).toString();
            String h = node.getClass().getMethod("getHeight").invoke(node).toString();
            System.out.println("(" + node.getClass().getName() + ") " + node);
            System.out.println("  " + w + "x" + h);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the count of each class in the given collection.
     * @param collection Collection.
     */
    private void debugPrintClasses(Collection<?> collection) {
        Map<Class, Integer> count = new HashMap<>();
        for (Object o : collection) {
            count.put(o.getClass(), 1+count.computeIfAbsent(o.getClass(), x -> 0));
        }
        System.out.println(count);
    }

}
