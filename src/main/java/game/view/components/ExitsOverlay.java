package game.view.components;

import game.model.Direction;
import game.util.Cache;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An overlay for directional exits for a particular tile.
 */
public class ExitsOverlay extends StackPane {
    /** Path to triangle image. Image is pointing north. */
    private static final String
            TRIANGLE_OVERLAY = "file:src/images/triangle_overlay.png";
    /** Map of Direction to that direction's rotation for the image. */
    private static final Map<Direction, Integer> rotation = new HashMap<>();
    static {
        rotation.put(Direction.north, 0);
        rotation.put(Direction.east, 90);
        rotation.put(Direction.south, 180);
        rotation.put(Direction.west, 270);
    }

    /** Directions whose exits have already been added to this overlay. */
    private final Set<Direction> exitsAdded = new HashSet<>();

    /**
     * Sets whether an exit is present in the given direction.
     * @param direction Direction of exit.
     * @param hasExit Whether exit is present.
     */
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
        // We don't need to support this because exits can't be removed
        // from a tile once its been loaded. Throw just in case.
        if (!hasExit && exitsAdded.contains(direction)) {
            throw new UnsupportedOperationException(
                    "Removing exits is not supported.");
        }
    }
}
