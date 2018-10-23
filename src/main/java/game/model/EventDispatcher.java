package game.model;

import game.util.Utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Manages event listeners and dispatches events to the correct listeners.
 * @param <T> Superclass of all events.
 */
public class EventDispatcher<T> {
    /**
     * Mapping of each event class to its listeners.
     */
    private final Map<Class<? extends T>, Set<Consumer<? extends T>>>
            eventListeners = new HashMap<>();

    /**
     * Returns the set of listeners for the given event class, creating
     * and inserting an empty set if it doesn't already exist.
     * @param eventType Event class.
     * @return Set of listeners.
     */
    private Set<Consumer<? extends T>> getOrInsert(Class<? extends T> eventType) {
        return eventListeners.computeIfAbsent(eventType, k -> new HashSet<>());
    }

    /**
     * Adds a listener, listening for a certain event type and all its
     * subclasses.
     * <p>
     * If eventType is null, the listener will be fired for all events.
     * In this case, the listener must accept a superclass of all event types
     * (for example, {@link T} or {@link Object}).
     * </p>
     * @param eventType Event type or null for any event.
     * @param listener Callback accepting an event object.
     * @param <U> Event type.
     */
    public <U extends T> void addListener(Class<U> eventType, Consumer<U> listener) {
        getOrInsert(eventType).add(listener);
    }

    /**
     * Removes a listener, from the given event type. Although a listener
     * can listen to subevents of its specified event, it must be removed
     * by specifying the original event type.
     * @param eventType Event type or null for any event.
     * @param listener Listener to remove.
     * @param <U> Event type the listener was registered with.
     */
    public <U extends T> void removeListener(Class<U> eventType, Consumer<U> listener) {
        getOrInsert(eventType).remove(listener);
    }

    /**
     * Notifies listeners listening to the given event's type or a superclass
     * of the event.
     * @param event Event object.
     * @param <U> Event type.
     */
    public <U extends T> void notifyListeners(U event) {
        for (Class<? extends T> eventType : eventListeners.keySet()) {
            // The use of generics here seems somewhat hacky.
            if (eventType == null || Utilities.isInstance(event, eventType)) {
                for (Consumer<? extends T> listener : eventListeners.get(eventType)) {
                    // Because we check using isInstance, we can be sure that
                    // this cast succeeds, except if an invalid null listener
                    // is added...

                    //noinspection unchecked
                    ((Consumer<U>)listener).accept(event);
                }
            }
        }
    }
}
