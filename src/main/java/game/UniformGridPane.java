package game;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class UniformGridPane extends GridPane {
    protected final int COLUMNS;
    protected final int ROWS;
    protected final double GAP;

    public UniformGridPane(int columns, int rows) {
        this(columns, rows, 5);
    }

    public UniformGridPane(int columns, int rows, double gap)
    {
        super();
        COLUMNS = columns;
        ROWS = rows;
        GAP = gap;
        Utilities.setMaxWidthHeight(this);

        this.setVgap(gap);
        this.setHgap(gap);

        applyRowAndColumnConstraints();

        this.prefHeightProperty().bind(this.widthProperty());
        this.setMaxHeight(Control.USE_PREF_SIZE);
        this.setMinHeight(Control.USE_PREF_SIZE);
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
