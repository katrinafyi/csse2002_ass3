package game.view.components;

import game.util.Utilities;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class UniformGridPane extends GridPane {
    public final int COLUMNS;
    public final int ROWS;

    public final int HALF_COLS;
    public final int HALF_ROWS;

    public final double GAP;

    public UniformGridPane(int columns, int rows) {
        this(columns, rows, 5);
    }

    public UniformGridPane(int columns, int rows, double gap)
    {
        super();
        COLUMNS = columns;
        ROWS = rows;
        HALF_COLS = (columns-1)/2;
        HALF_ROWS = (ROWS-1)/2;
        GAP = gap;

        Utilities.setMaxWidthHeight(this);

        this.setVgap(gap);
        this.setHgap(gap);
        this.setAlignment(Pos.CENTER);


        applyRowAndColumnConstraints();
    }

    public void fixHeightToWidth() {
        this.prefHeightProperty().bind(this.widthProperty());
    }

    private void applyRowAndColumnConstraints() {
        for (int i = 0; i < COLUMNS; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / COLUMNS);
            this.getColumnConstraints().add(columnConstraints);
        }
        for (int j = 0; j < ROWS; j++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / ROWS);
            this.getRowConstraints().add(rowConstraints);
        }
    }
}
