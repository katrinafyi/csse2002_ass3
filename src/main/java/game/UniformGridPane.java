package game;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public abstract class UniformGridPane extends GridPane {
    protected final int COLUMNS;
    protected final int ROWS;

    public UniformGridPane(int columns, int rows)
    {
        super();
        COLUMNS = columns;
        ROWS = rows;
        Utilities.setMaxWidthHeight(this);

        this.minHeightProperty().bind(this.widthProperty());
        this.setPrefHeight(this.getWidth());

        this.setVgap(5);
        this.setHgap(5);

        generateGrid();
    }

    protected abstract Node generateCell(int col, int row);

    private void generateGrid() {
        for (int i = 0; i < COLUMNS; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / COLUMNS);
            this.getColumnConstraints().add(columnConstraints);

            for (int j = 0; j < ROWS; j++) {
                if (i == 0) {
                    RowConstraints rowConstraints = new RowConstraints();
                    rowConstraints.setPercentHeight(100.0 / ROWS);
                    this.getRowConstraints().add(rowConstraints);
                }
                Node cell = generateCell(i, j);
                if (cell != null) {
                    this.add(cell, i, j);
                }
            }
        }
    }
}
