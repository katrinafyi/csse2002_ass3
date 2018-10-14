package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Model {
    // Structure from https://www.codeproject.com/Articles/42830/Model-View-Controller-Model-View-Presenter-and-Mod

    public static abstract class Event {}

    private final Map<Class<? extends Event>, List<View>> views = new HashMap<>();

    private void insertListIfAbsent(Class<? extends Event> key) {
        views.computeIfAbsent(key, e -> new ArrayList<>());
    }

    public void addView(Class<? extends Event> eventType, View view) {
        insertListIfAbsent(eventType);
        views.get(eventType).add(view);
    }

    public void removeView(Class<? extends Event> eventType, View view) {
        insertListIfAbsent(eventType);
        views.get(eventType).remove(view);
    }

    public void notifyViews(Event event) {
        insertListIfAbsent(event.getClass());
        for (View view : views.get(event.getClass())) {
            view.update(event);
        }
    }
}
