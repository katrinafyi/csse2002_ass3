package game;

import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldActions;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileNotFoundException;


public class MainApplication extends Application {

    private Stage primaryStage;

    private BlockWorldActions worldInteraction;

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;


        primaryStage.setTitle("Window title");

        GridPane rootGrid = new GridPane();
        rootGrid.setPadding(new Insets(10));
        rootGrid.setHgap(20);

        // Container for menu and main content.
        VBox container = new VBox();
        MenuBar menuBar = constructMenuBar();
        container.getChildren().addAll(menuBar, rootGrid);

        WorldMapGridPane worldMapView = new WorldMapGridPane();
        rootGrid.add(worldMapView, 0, 0);
        GridPane.setValignment(worldMapView, VPos.TOP);

        BuilderControlsPane centrePane = new BuilderControlsPane();
        rootGrid.add(centrePane, 1, 0);
        GridPane.setValignment(centrePane, VPos.TOP);

        InventoryPane rightPane = new InventoryPane();
        rootGrid.add(rightPane, 2, 0);
        GridPane.setValignment(rightPane, VPos.TOP);

        centrePane.setMaxWidth(Double.MAX_VALUE);

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

        Scene scene = new Scene(container, 800, 600, true, SceneAntialiasing.BALANCED);
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(622);
        primaryStage.minHeightProperty().bind(worldMapView.widthProperty().add(80.5));

        scene.setOnKeyPressed(e -> {
            int direction = 0;
            if (e.getCode() == KeyCode.RIGHT) {
                direction = -1;
            } else if (e.getCode() == KeyCode.LEFT) {
                direction = +1;
            }

            if (direction != 0) {
            }

            int up = 0;
            if (e.getCode() == KeyCode.UP)
                up = 1;
            else if (e.getCode() == KeyCode.DOWN)
                up = -1;

            if (up != 0) {
            }

        });

        primaryStage.setHeight(580.5);
        primaryStage.show();
    }

    private void openMapAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open map");
        String filePath = fileChooser.showOpenDialog(primaryStage).getPath();
        try {
            worldInteraction.loadWorldMapFile(filePath);
        } catch (WorldMapFormatException | WorldMapInconsistentException e) {
            showErrorMessage(e.toString());
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private void showErrorMessage(String message) {
        System.err.println("ERROR MESSAGE: " + message);
    }

    private MenuBar constructMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem openMap = new MenuItem("Open map");
        openMap.setOnAction(this::openMapAction);

        fileMenu.getItems().addAll(
                new MenuItem("Open map"),
                new MenuItem("Save map"),
                new MenuItem("Save map as"),
                new SeparatorMenuItem(),
                new MenuItem("Exit")
        );
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }
}
