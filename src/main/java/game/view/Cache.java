package game.view;


import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.function.Function;

public class Cache<K, V> extends HashMap<K, V> {
    private static final Cache<String, Image> imageCache = new Cache<>(Image::new);

    private final Function<K, V> defaultGenerator;

    public Cache(Function<K, V> defaultGenerator) {
        this.defaultGenerator = defaultGenerator;
    }

    @Override
    public V get(Object url) {
        V image = super.get(url);
        if (image == null) {
            image = defaultGenerator.apply((K)url);
            this.put((K)url, image);
        }
        return image;
    }

    public static Cache<String, Image> getImageCache() {
        return imageCache;
    }
}
