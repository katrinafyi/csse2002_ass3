package game.util;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Miscellaneous common functions relating to JavaFX and other things.
 */
public class Utilities {
    /**
     * Sets both the maximum width and height of the given region to
     * {@link Double#MAX_VALUE}.
     * @param region Region to operate on.
     */
    public static void setMaxWidthHeight(Region region) {
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMaxHeight(Double.MAX_VALUE);
    }

    /**
     * Sets the background of the given region to a solid colour.
     * @param region Region to set background of.
     * @param color Background colour.
     */
    public static void setBackground(Region region, Color color) {
        region.setBackground(new Background(new BackgroundFill(
                color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * Sets the border of the given region to a solid colour.
     * @param region Region to set border of.
     * @param color Border colour.
     */
    public static void setBorder(Region region, Color color) {
        region.setBorder(new Border(new BorderStroke(color,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
    }

    /**
     * Binds the min/max width/height of the given node to their preferred
     * values.
     * @param node Region to alter.
     */
    public static void usePrefWidthHeight(Region node) {
        node.setMinWidth(Control.USE_PREF_SIZE);
        node.setMaxWidth(Control.USE_PREF_SIZE);
        node.setMinHeight(Control.USE_PREF_SIZE);
        node.setMaxHeight(Control.USE_PREF_SIZE);
    }

    /**
     * Adds a listener to the given property, which only fires after the pause
     * transition has elapsed and no changes to the property have occurred.
     *
     * <p>
     *     This prevents excessive calls to expensive layout computations.
     *     Inspired by underscore.js's debounce() function.
     * </p>
     * @param pause Transition to pause on.
     * @param property Property to observe.
     * @param listener Callback.
     * @param <T> Type of property.
     */
    public static <T> void delayBinding(PauseTransition pause,
                                        ObservableValue<T> property,
                                        ChangeListener<? super T> listener) {
        property.addListener((prop, oldValue, newValue) -> {
            pause.setOnFinished(e -> listener.changed(prop, oldValue, newValue));
            pause.playFromStart();
        });
    }

    /**
     * Checks if the given object is an instance of, or some subclass of,
     * the superclass.
     *
     * <p>Essentially equivalent to Python's
     * {@literal isinstance(object, superclass)}</p>
     * @param object Object to test.
     * @param superclass Superclass object.
     * @return Whether object is some superclass of the given class.
     */
    public static boolean isInstance(Object object, Class<?> superclass) {
        return superclass.isAssignableFrom(object.getClass());
    }

    /**
     * Returns the given string with its first letter capitalised.
     * @param string String.
     * @return String with first letter capitalised.
     */
    public static String capitalise(String string) {
        // From https://stackoverflow.com/a/5725949
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
