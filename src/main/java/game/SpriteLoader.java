package game;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {
    private static final SpriteLoader globalLoader = new SpriteLoader();

    private final Map<String, Image> imageCache = new HashMap<>();

    public Image loadImage(String url) {
        Image image = imageCache.get(url);
        if (image == null) {
            image = new Image(url);
            imageCache.put(url, image);
        }
        return image;
    }

    public static SpriteLoader getGlobalLoader() {
        return globalLoader;
    }
}
