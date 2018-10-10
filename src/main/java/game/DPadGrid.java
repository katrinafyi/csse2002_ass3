package game;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DPadGrid extends UniformGridPane {
    public DPadGrid() {
        super(3, 3);
    }

    protected Node generateCell(int col, int row) {
        Button b = new Button("<--");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMaxHeight(Double.MAX_VALUE);
        return b;
    }
}
