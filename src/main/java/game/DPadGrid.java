package game;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DPadGrid extends UniformGridPane {
    public DPadGrid() {
        super(3, 3);
    }

    protected Node generateCell(int col, int row) {
        if (Math.abs(col-1) == Math.abs(row-1)) {
            return null;
        }
        Button b = new Button("<--");
        Utilities.setMaxWidthHeight(b);
        b.prefHeightProperty().bind(b.widthProperty());
        return b;
    }
}
