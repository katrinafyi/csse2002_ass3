package game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MainApplication extends Application {

    private GridPane worldMap;
    private Stage primaryStage;

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

        SubScene worldMap = new WorldMap3DGroup().generateScene();
        rootGrid.add(worldMap, 0, 0);
        GridPane.setValignment(worldMap, VPos.TOP);

        Region centrePane = constructCentrePane();
        rootGrid.add(centrePane, 1, 0);
        GridPane.setValignment(centrePane, VPos.TOP);

        Region rightPane = constructRightPane();
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

        primaryStage.setMinWidth(622);
        primaryStage.minHeightProperty().bind(worldMap.widthProperty().add(80.5));

        primaryStage.setHeight(580.5);
        primaryStage.show();
    }

    private MenuBar constructMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
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

    private Region constructCentrePane() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);
        DPadGrid grid = new DPadGrid();

        Button debug = new Button("(Debug)");
        debug.setOnAction((e) -> {
            System.out.println(worldMap);
            System.out.println(""+primaryStage.getWidth() + "x"+primaryStage.getHeight());
            System.out.println(""+worldMap.getWidth() + "x"+worldMap.getHeight());
        });

        Button b2 = new Button("(Size to scene)");
        b2.setOnAction(e -> primaryStage.sizeToScene());

        vbox.getChildren().addAll(grid, new Button("DIG"), debug, b2);
        return vbox;
    }

    private Region constructRightPane() {
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label("Inventory"));
        return vbox;
    }
}
