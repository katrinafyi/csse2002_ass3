package game.view.components;

import game.util.Utilities;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class UniformGridPane extends GridPane {
    public final int columns;
    public final int rows;

    public final int halfCols;
    public final int halfRows;

    public final double gap;

    public UniformGridPane(int columns, int rows) {
        this(columns, rows, 5);
    }

    public UniformGridPane(int columns, int rows, double gap)
    {
        super();
        this.columns = columns;
        this.rows = rows;
        halfCols = (columns-1)/2;
        halfRows = (this.rows -1)/2;
        this.gap = gap;

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
        for (int i = 0; i < columns; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / columns);
            this.getColumnConstraints().add(columnConstraints);
        }
        for (int j = 0; j < rows; j++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / rows);
            this.getRowConstraints().add(rowConstraints);
        }
    }
}
