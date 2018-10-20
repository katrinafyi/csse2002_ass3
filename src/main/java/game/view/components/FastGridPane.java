package game.view.components;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class FastGridPane extends Pane {
    public final int columns;
    public final int rows;



    public FastGridPane(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public void add(Node node, double col, double row) {
        if (col < 0 || col >= columns || row < 0 || row >= rows) {
            throw new IllegalArgumentException("Invalid row or column.");
        }

        if (!this.getChildren().contains(node)) {
            this.getChildren().add(node);
        }
        node.relocate(col*getWidth()/columns, row*getHeight()/rows);
    }
}
