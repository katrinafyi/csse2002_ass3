package game.view.components;

import game.util.Cache;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Button with an image as its content.
 */
public class IconButton extends Button {
    /**
     * Constructs a new {@link IconButton} of width 40 using the given image
     * URL.
     * @param imageUrl URL of image.
     */
    public IconButton(String imageUrl) {
        this(imageUrl, 40);
    }

    /**
     * Constructs a new {@link IconButton} with the given width and inner
     * image.
     * @param imageUrl URL of image to use.
     * @param width Width of image.
     */
    public IconButton(String imageUrl, double width) {
        Image image = Cache.getImageCache().get(imageUrl);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);

        setGraphic(imageView);
        setPadding(new Insets(3));
    }
}
