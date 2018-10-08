package game;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;


public class MainApplication extends Application {
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Window title");
        GridPane grid = new GridPane();

        Button worldMap = new Button("World Map");
        worldMap.setMaxWidth(Double.MAX_VALUE);
        worldMap.setMaxHeight(Double.MAX_VALUE);
        worldMap.minHeightProperty().bind(worldMap.widthProperty());


        Button b = new Button("DIG");

        grid.add(worldMap, 0, 0);
        GridPane.setValignment(worldMap, VPos.TOP);
        grid.add(b, 1, 0);
        GridPane.setValignment(b, VPos.TOP);

        b.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHgrow(Priority.ALWAYS);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(100);

        grid.getColumnConstraints().addAll(col0, col1, col2);
        RowConstraints row0 = new RowConstraints();
        row0.minHeightProperty().bind(worldMap.widthProperty());
        grid.getRowConstraints().add(row0);

        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(300);
        primaryStage.show();
    }
}
