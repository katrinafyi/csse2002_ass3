package game;

import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldController;
import game.controller.GameController;
import game.model.GameModel;
import game.view.GameControlsPane;
import game.view.GameInventoryPane;
import game.view.GameMenuBar;
import game.view.GameWorldMapView;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
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
import javafx.util.Duration;

import java.io.FileNotFoundException;


public class MainApplication extends Application {

    private Stage primaryStage;
    private GameController controller;

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;


        primaryStage.setTitle("Window title");

        GridPane rootGrid = new GridPane();
        rootGrid.setPadding(new Insets(10));
        rootGrid.setHgap(20);
//        rootGrid.setStyle("-fx-background-color: purple;");

        GameModel model = new GameModel();
        controller = new GameController(model);

        // Container for menu and main content.
        VBox container = new VBox();
        MenuBar menuBar = new GameMenuBar(primaryStage, model,
                controller, controller);
        container.getChildren().addAll(menuBar, rootGrid);
        VBox.setVgrow(rootGrid, Priority.ALWAYS);

        GameWorldMapView worldMapView = new GameWorldMapView(model);

        VBox worldMapContainer = new VBox();
        worldMapContainer.setStyle("-fx-background-color: yellow;");
        worldMapView.setStyle("-fx-border-color: black;");
        Utilities.setMaxWidthHeight(worldMapContainer);
        worldMapContainer.getChildren().add(worldMapView);
        worldMapContainer.setAlignment(Pos.TOP_CENTER);

        rootGrid.add(worldMapContainer, 0, 0);


        GameControlsPane centrePane = new GameControlsPane(model, controller, controller);
        rootGrid.add(centrePane, 1, 0);
        GridPane.setValignment(centrePane, VPos.TOP);

        GameInventoryPane rightPane = new GameInventoryPane(model, controller, controller);
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

        Scene scene = new Scene(container);
        primaryStage.setScene(scene);

        ChangeListener<Number> setWidth = (a, b, c) -> {
            double size = Math.min(worldMapContainer.getWidth(), worldMapContainer.getHeight());
            size = Math.min(size, scene.getWidth()-360);
            size = Math.min(size, scene.getHeight()-45);
            size = Math.max(size, 1);
            System.out.println(size);
            worldMapView.setPrefWidth(size);
            worldMapView.setPrefHeight(size);
        };

        Utilities.delayBinding(new PauseTransition(new Duration(200)),
                worldMapContainer.widthProperty(), setWidth);
        Utilities.delayBinding(new PauseTransition(new Duration(200)),
                worldMapContainer.heightProperty(), setWidth);


        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                System.out.println(worldMapContainer);
                System.out.println(scene);
                System.out.println(primaryStage);
            }
        });


        primaryStage.setHeight(580.5);
        primaryStage.show();
    }

}
