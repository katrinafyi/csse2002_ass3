package game;

import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class WorldMapGrid extends GridPane {

    private static final int COLUMNS = 4;
    private static final int ROWS = 8;

    public WorldMapGrid()
    {
        super();
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        this.minHeightProperty().bind(this.widthProperty());

        this.setVgap(5);
        this.setHgap(5);

        generateGrid();
    }

    private void generateGrid() {
        for (int i = 0; i < COLUMNS; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0/COLUMNS);
            this.getColumnConstraints().add(columnConstraints);

            for (int j = 0; j < ROWS; j++) {
                if (i == 0) {
                    RowConstraints rowConstraints = new RowConstraints();
                    rowConstraints.setPercentHeight(100.0 / ROWS);
                    this.getRowConstraints().add(rowConstraints);
                }

                Button button = new Button(""+i+","+j);
                button.setMaxHeight(Double.MAX_VALUE);
                button.setMaxWidth(Double.MAX_VALUE);
                this.add(button, i, j);
            }
        }
    }

}
