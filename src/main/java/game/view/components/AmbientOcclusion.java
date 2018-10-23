package game.view.components;

import game.model.Direction;
import game.util.Cache;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Ambient occlusion overlay for one tile. Implementing by rotating and
 * overlaying two images, a full edge gradient and a corner gradient.
 */
public class AmbientOcclusion extends StackPane {
    // Implementation based on:
    // https://0fps.net/2013/07/03/ambient-occlusion-for-minecraft-like-worlds/

    /** Index of each direction. Used in computations. */
    private static final Map<Direction, Integer> index = new HashMap<>();
    static {
        index.put(Direction.north, 0);
        index.put(Direction.east, 1);
        index.put(Direction.south, 2);
        index.put(Direction.west, 3);
    }

    /** Map of a direction to its image layer. */
    private final Map<Direction, ImageView> layers = new HashMap<>();

    /**
     * Constructs a new ambient occlusion overlay with no shadows.
     * The width of the shadow images is bound to this node's
     * {@link #maxWidthProperty()}.
     */
    public AmbientOcclusion() {
        for (Direction dir : Direction.values()) {
            ImageView imageView = new ImageView();
            imageView.setRotate(90*index.get(dir));
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(this.maxWidthProperty());

            this.getChildren().add(imageView);
            layers.put(dir, imageView);
        }
    }

    /**
     * Updates the ambient occlusion of this overlay with the given adjacent
     * heights.
     *
     * <p>
     *     The adjacent parameter is an array of length 8 containing how many
     *     blocks each adjacent tile is higher than this tile, starting at the
     *     tile north-west and continuing clockwise. For example, (this tile
     *     is the center number)
     *     <pre>{@code
     *3 2 4
     *4 4 5
     *5 2 8
     *     }</pre>
     *     Then, {@literal adjacent = { -1, -2, 0, 1, 3, -2, 1, 0 }}.
     * </p>
     * @param adjacent Array of length 8 as described above.
     */
    public void setAdjacent(int[] adjacent) {
        for (Direction direction : Direction.values()) {
            applyAO(adjacent, direction);
        }
    }

    /**
     * Shortcut for mod 8.
     * @param i Input integer.
     * @return i % 8, ensuring the result is positive.
     */
    private static int mod(int i) {
        return Math.floorMod(i, 8); // Ensures positive modulus.
    }

    /**
     * Applies AO only considering one cardinal direction. Considers blocks
     * in the given direction and counterclockwise.
     * @param adjacent Adjacent array.
     * @param direction Direction to consider.
     * @see #setAdjacent(int[])
     */
    private void applyAO(int[] adjacent, Direction direction) {
        /*

        Adjacent is an array representing relative heights of tiles
        adjacent to this one, in the following order:

        0 1 2
        7 . 3
        6 5 4

        Thus, `shift` represents the index of the corner block anticlockwise
        from `direction`.

        */
        int shift = 2*index.get(direction);
        if (adjacent[shift+1] > 0) {
            // Block is present on the side of this direction.
            // Draw edge gradient.
            setLayerImage(direction, "file:src/images/ao_n_full.png");
        } else if (adjacent[shift] > 0 && !(adjacent[mod(shift-1)] > 0)) {
            // Corner anti-clockwise from this direction is present
            // and the other side of that corner is not.
            // If the other side was present, a different iteration of this
            // would draw the full edge shadow.
            setLayerImage(direction, "file:src/images/ao_nw_corner.png");
        } else {
            // Remove any previously set shadow in this direction.
            setLayerImage(direction, null);
        }
    }

    /**
     * Draws the image given by the imageUrl to the layer of the given
     * direction, using the global image cache.
     * @param direction Direction.
     * @param imageUrl Image path.
     */
    private void setLayerImage(Direction direction, String imageUrl) {
        layers.get(direction).setImage(imageUrl == null ? null
                : Cache.getImageCache().get(imageUrl));
    }
}

