package game.model;

import game.Utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class EventDispatcher<T> {
    private final Map<Class<? extends T>, Set<Consumer<T>>> eventListeners = new HashMap<>();

    private Set<Consumer<T>> getOrInsert(Class<? extends T> eventType) {
        return eventListeners.computeIfAbsent(eventType, k -> new HashSet<>());
    }

    public void addListener(Class<? extends T> eventType, Consumer<T> listener) {
        getOrInsert(eventType).add(listener);
    }

    public void removeListener(Class<? extends T> eventType, Consumer<T> listener) {
        getOrInsert(eventType).remove(listener);
    }

    public void notifyListeners(T event) {
        for (Class<? extends T> eventType : eventListeners.keySet()) {
            if (eventType == null || Utilities.isInstance(event, eventType)) {
                for (Consumer<T> tConsumer : eventListeners.get(eventType)) {
                    tConsumer.accept(event);
                }
            }
        }
    }
}
