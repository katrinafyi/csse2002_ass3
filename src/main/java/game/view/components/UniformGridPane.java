package game.view.components;

import game.util.Utilities;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 * Grid with evenly distributed rows and columns.
 */
public class UniformGridPane extends GridPane {
    /** Number of columns. */
    public final int columns;
    /** Number of rows. */
    public final int rows;

    /** Half number of colunms. */
    public final int halfCols;
    /** Half number of rows. */
    public final int halfRows;

    /** Gap between columns and rows. */
    public final double gap;

    /**
     * Constructs a new {@link UniformGridPane} with the given number of
     * rows and columns and a margin of 5.
     * @param columns Columns.
     * @param rows Rows.
     */
    public UniformGridPane(int columns, int rows) {
        this(columns, rows, 5);
    }

    /**
     * Constructs a new {@link UniformGridPane} with the given number of
     * rows and columns and given margin.
     * @param columns Columns.
     * @param rows Rows.
     * @param gap Gap between each column and row.
     */
    public UniformGridPane(int columns, int rows, double gap)
    {
        super();
        this.columns = columns;
        this.rows = rows;
        this.halfCols = columns/2; // Implicitly rounds down.
        this.halfRows = rows/2;
        this.gap = gap;

        Utilities.setMaxWidthHeight(this);

        this.setVgap(gap);
        this.setHgap(gap);
        this.setAlignment(Pos.CENTER);

        applyRowAndColumnConstraints();
    }

    /**
     * Bind this node's preferred height to its width, hopefully resulting in
     * a square grid.
     */
    public void fixHeightToWidth() {
        this.prefHeightProperty().bind(this.widthProperty());
    }

    /**
     * Applies row and column constraints to ensure all rows and columns are
     * evenly sized.
     */
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
