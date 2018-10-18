package game.view.components;

import game.model.Direction;
import game.view.SpriteLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

// Implementation based on:
// https://0fps.net/2013/07/03/ambient-occlusion-for-minecraft-like-worlds/
public class AmbientOcclusion extends StackPane {
    private static final Map<Direction, Integer> index = new HashMap<>();
    static {
        index.put(Direction.north, 0);
        index.put(Direction.east, 1);
        index.put(Direction.south, 2);
        index.put(Direction.west, 3);
    }

    private final Map<Direction, ImageView> layers = new HashMap<>();

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

    public void setAdjacent(int[] adjacent) {
        for (Direction direction : Direction.values()) {
            applyAO(adjacent, direction);
        }
    }

    private int mod(int i) {
        return Math.floorMod(i, 8); // Ensures positive modulus.
    }

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
            System.out.println(direction + " side");
            setLayerImage(direction, "file:src/images/ao_n_full.png");
        } else if (adjacent[shift] > 0 && !(adjacent[mod(shift-1)] > 0)) {
            // Corner anti-clockwise from this direction is present
            // and the other side of this corner is not.
            System.out.println(direction + " corner");
            setLayerImage(direction, "file:src/images/ao_nw_corner.png");
        }
    }

    private void setLayerImage(Direction direction, String imageUrl) {
        layers.get(direction).setImage(
                SpriteLoader.getGlobalLoader().loadImage(imageUrl));
    }
}
