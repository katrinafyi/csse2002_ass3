package game.util;


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
        return computeIfAbsent((K)url, defaultGenerator);
    }

    public static Cache<String, Image> getImageCache() {
        return imageCache;
    }
}
