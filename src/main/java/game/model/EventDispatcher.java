package game.model;

import game.util.Utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class EventDispatcher<T> {
    private final Map<Class<? extends T>, Set<Consumer<? extends T>>> eventListeners = new HashMap<>();

    private Set<Consumer<? extends T>> getOrInsert(Class<? extends T> eventType) {
        return eventListeners.computeIfAbsent(eventType, k -> new HashSet<>());
    }

    public <U extends T> void addListener(Class<U> eventType, Consumer<U> listener) {
        getOrInsert(eventType).add(listener);
    }

    public <U extends T> void removeListener(Class<U> eventType, Consumer<U> listener) {
        getOrInsert(eventType).remove(listener);
    }

    public <U extends T> void notifyListeners(U event) {
        for (Class<? extends T> eventType : eventListeners.keySet()) {
            // The use of generics here is... not great :/
            if (eventType == null || Utilities.isInstance(event, eventType)) {
                for (Consumer<? extends T> listener : eventListeners.get(eventType)) {
                    // Because we check
                    //noinspection unchecked
                    ((Consumer<U>)listener).accept(event);
                }
            }
        }
    }
}
