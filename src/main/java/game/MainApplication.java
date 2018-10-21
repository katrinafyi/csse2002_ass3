package game;

import game.controller.GameController;
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


public class MainApplication extends Application {

    private Stage primaryStage;
    private GameModel model;
    private GameController controller;
    
    private GameMenuBar menuBar;
    private GameWorldMapView worldMapView;
    private VBox worldMapContainer;
    private GameControlsPane controlsPane;
    private GameInventoryPane inventoryPane;

    private Scene scene;
    
    private final Map<KeyCode, Button> keyBindings = new HashMap<>();

    private double verticalExtra;
    private double horizontalExtra;
    private double hGap = 20;
    private double gridPadding = 10;

    private boolean debugEnabled = false;

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;


        primaryStage.setTitle("Window title");

        GridPane rootGrid = new GridPane();
        rootGrid.setPadding(new Insets(gridPadding));
        rootGrid.setHgap(hGap);
//        rootGrid.setStyle("-fx-background-color: purple;");

        model = new GameModel();
        controller = new GameController(model);

        // Container for menu and main content.
        VBox container = new VBox();
        menuBar = new GameMenuBar(primaryStage, model,
                controller, controller);
        container.getChildren().addAll(menuBar, rootGrid);
        VBox.setVgrow(rootGrid, Priority.ALWAYS);

        worldMapView = new GameWorldMapView(model);
        Utilities.setBorder(worldMapView, Color.BLACK);

        worldMapContainer = new VBox();
        Utilities.setMaxWidthHeight(worldMapContainer);
        worldMapContainer.getChildren().add(worldMapView);
        worldMapContainer.setAlignment(Pos.TOP_CENTER);

        rootGrid.add(worldMapContainer, 0, 0);


        controlsPane = new GameControlsPane(model, controller, controller);
        controlsPane.setDisable(true);
        rootGrid.add(controlsPane, 1, 0);
        GridPane.setValignment(controlsPane, VPos.TOP);
        GridPane.setVgrow(controlsPane, Priority.NEVER);

        inventoryPane = new GameInventoryPane(model, controller, controller);
        inventoryPane.setDisable(true);
        rootGrid.add(inventoryPane, 2, 0);
        GridPane.setValignment(inventoryPane, VPos.TOP);

        model.addListener(WorldMapLoadedEvent.class, this::activateControls);

        controlsPane.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(150);
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(150);
        col2.setMinWidth(150);
        rootGrid.getColumnConstraints().addAll(col0, col1, col2);

        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(100);
        rootGrid.getRowConstraints().add(row0);

        scene = new Scene(container);
        primaryStage.setScene(scene);


        PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
        Utilities.<Number>delayBinding(pauseTransition,
                primaryStage.widthProperty(), this::setWorldMapViewDimensions);
        Utilities.<Number>delayBinding(pauseTransition,
                primaryStage.heightProperty(), this::setWorldMapViewDimensions);

        setKeyBindings();

        scene.setOnKeyPressed(this::keyPressHandler);

        primaryStage.setHeight(580.5);
        primaryStage.show();

        verticalExtra = primaryStage.getHeight() - scene.getHeight();
        horizontalExtra = primaryStage.getWidth() - scene.getWidth();
        System.out.println(verticalExtra + " | " + horizontalExtra);
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(795);
    }

    private void activateControls(BaseBlockWorldEvent e) {
        controlsPane.setDisable(false);
        inventoryPane.setDisable(false);
    }


    private void setWorldMapViewDimensions(ObservableValue<? extends Number> observable,
                                           Number oldValue, Number newValue) {
        int cols = worldMapView.columns;
        double size = Math.min(worldMapContainer.getWidth(),
                worldMapContainer.getHeight());
        size = Math.min(size, scene.getWidth()-360);
        size = Math.min(size, scene.getHeight()-45);
        size = Math.max(size, cols);
        // Round down to nearest multiple of number of columns for
        // seamless edges.
        size = cols*Math.floor(size/cols);

        worldMapView.setPrefWidth(size);
        worldMapView.setPrefHeight(size);
    }

    private void keyPressHandler(KeyEvent keyEvent) {
        // Debug layout.
        // Get and click the equivalent button.
        Button actionButton = keyBindings.get(keyEvent.getCode());
        if (actionButton != null) {
            actionButton.fire();
        }

        switch (keyEvent.getCode()) {
            case Z:
                worldMapView.setHeightsVisible(!worldMapView.isHeightsVisible());
                break;
            case X:
                worldMapView.setExitsVisible(!worldMapView.isExitsVisible());
                break;
            case OPEN_BRACKET: // Print layout info.
                debugPrintSize(worldMapView);
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
        }
    }

    private void toggleDebugLayout() {
        debugEnabled = !debugEnabled;
        if (debugEnabled) {
            Utilities.setBackground(worldMapContainer, Color.PURPLE);
            Utilities.setBackground(controlsPane, Color.GREEN);
            Utilities.setBackground(inventoryPane, Color.YELLOW);
        } else {
            Utilities.setBackground(worldMapContainer, null);
            Utilities.setBackground(controlsPane, null);
            Utilities.setBackground(inventoryPane, null);
        }
    }

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

    private void debugPrintClasses(Collection<?> collection) {
        Map<Class, Integer> count = new HashMap<>();
        for (Object o : collection) {
            count.put(o.getClass(), 1+count.computeIfAbsent(o.getClass(), x -> 0));
        }
        System.out.println(count);
    }

}
