package game;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class DPadGrid extends UniformGridPane {
    private static final Map<Pair<Integer, Integer>, String> labels = new HashMap<>();

    static {
        labels.put(new Pair<>(0, -1), "↑");
        labels.put(new Pair<>(0, 1), "↓");
        labels.put(new Pair<>(-1, 0), "←");
        labels.put(new Pair<>(1, 0), "→");
    }

    public DPadGrid() {
        super(3, 3);
    }

    protected Node generateCell(int col, int row) {
        if (Math.abs(col-1) == Math.abs(row-1)) {
            // Do not add buttons to corners or the centre.
            return null;
        }

        Button b = new Button(labels.get(new Pair<>(col-1, row-1)));
        b.setStyle("font-size: 20pt; font-weight: bold;");
        Utilities.setMaxWidthHeight(b);
        b.prefHeightProperty().bind(b.widthProperty());
        return b;
    }
}
