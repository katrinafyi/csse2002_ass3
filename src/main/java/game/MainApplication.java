package game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MainApplication extends Application {

    private GridPane worldMap;

    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Window title");
        GridPane rootGrid = new GridPane();
        rootGrid.setPadding(new Insets(10));
        rootGrid.setHgap(10);

        worldMap = new WorldMapGridPane();


        rootGrid.add(worldMap, 0, 0);
        GridPane.setValignment(worldMap, VPos.TOP);

        Region centrePane = constructCentrePane();
        rootGrid.add(centrePane, 1, 0);
        GridPane.setValignment(centrePane, VPos.TOP);

        centrePane.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(150);
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(150);

        rootGrid.getColumnConstraints().addAll(col0, col1, col2);
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(100);
        row0.setVgrow(Priority.ALWAYS);
        rootGrid.getRowConstraints().add(row0);

        Scene scene = new Scene(rootGrid);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.minHeightProperty().bind(worldMap.widthProperty().add(20));
        primaryStage.show();
    }

    private Region constructCentrePane() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);
        DPadGrid grid = new DPadGrid();

        Button debug = new Button("(Debug)");
        debug.setOnAction((e) -> {
            System.out.println(worldMap);
        });

        vbox.getChildren().addAll(grid, new Button("DIG"), debug);
        return vbox;
    }
}
