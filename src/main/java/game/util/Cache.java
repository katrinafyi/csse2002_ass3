package game.util;


import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.function.Function;

/**
 * A cache-like data object, backed by a {@link HashMap}.
 * Caches and returns results of the given generator function when
 * {@link #get(Object)} is called.
 *
 * <p>Essentially, this is Python's {@code collections.defaultdict}.</p>
 * @param <K> Key type (function input).
 * @param <V> Value type (result of function).
 */
public class Cache<K, V> extends HashMap<K, V> {
    /**
     * Global cache of JavaFX images.
     */
    private static final Cache<String, Image>
            imageCache = new Cache<>(Image::new);

    /**
     * Function to cache results of. Used to generate missing values.
     */
    private final Function<K, V> defaultGenerator;

    /**
     * Constructs a new {@link Cache}, caching results of the given function.
     * @param defaultGenerator Function to cache.
     */
    public Cache(Function<K, V> defaultGenerator) {
        this.defaultGenerator = defaultGenerator;
    }

    /**
     * Gets the value at the given key. If the key isn't contained in this
     * cache, the defaultGenerator is called with the key and the result
     * is stored then returned.
     * @param key Key object.
     * @return Value.
     */
    @Override
    public V get(Object key) {
        //noinspection unchecked
        return computeIfAbsent((K)key, defaultGenerator);
    }

    /**
     * Returns the global JavaFX image cache.
     * @return Cache of image paths to {@link Image} objects.
     */
    public static Cache<String, Image> getImageCache() {
        return imageCache;
    }
}
