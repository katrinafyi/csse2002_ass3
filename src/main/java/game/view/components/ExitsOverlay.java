package game.view.components;

import game.model.Direction;
import game.util.Cache;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExitsOverlay extends StackPane {
    private static final String TRIANGLE_OVERLAY = "file:src/images/triangle_overlay.png";
    private static final Map<Direction, Integer> rotation = new HashMap<>();
    static {
        rotation.put(Direction.north, 0);
        rotation.put(Direction.east, 90);
        rotation.put(Direction.south, 180);
        rotation.put(Direction.west, 270);
    }

    private final Set<Direction> exitsAdded = new HashSet<>();

    public void setHasExit(Direction direction, boolean hasExit) {
        if (hasExit && !exitsAdded.contains(direction)) {
            exitsAdded.add(direction);
            Image image = Cache.getImageCache().get(TRIANGLE_OVERLAY);
            ImageView imageView = new ImageView(image);

            imageView.setRotate(rotation.get(direction));
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(prefWidthProperty());
            imageView.fitHeightProperty().bind(prefHeightProperty());

            getChildren().add(imageView);
        }
        if (!hasExit && exitsAdded.contains(direction)) {
            throw new UnsupportedOperationException(
                    "Removing exits is not supported.");
        }
    }
}
