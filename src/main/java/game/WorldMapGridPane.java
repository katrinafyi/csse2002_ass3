package game;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class WorldMapGridPane extends UniformGridPane {
    public WorldMapGridPane() {
        super(9, 9);
    }

    protected Node generateCell(int col, int row) {
        Button b = new Button();
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMaxHeight(Double.MAX_VALUE);
        return b;
    }
}
