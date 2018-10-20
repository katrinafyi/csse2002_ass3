package game;

import csse2002.block.world.Position;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class Utilities {
    public static void setMaxWidthHeight(Region region) {
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMaxHeight(Double.MAX_VALUE);
    }

    public static Position addPos(Position p1, Position p2) {
        return new Position(p1.getX()+p2.getX(), p1.getY()+p2.getY());
    }

    public static Position negPos(Position p) {
        return new Position(-p.getX(), -p.getY());
    }

    public static Position subPos(Position p1, Position p2) {
        return addPos(p1, negPos(p2));
    }

    public static boolean isInstance(Object object, Class<?> superclass) {
        return superclass.isAssignableFrom(object.getClass());
    }

    public static String capitalise(String s) {
        // From https://stackoverflow.com/a/5725949
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static <T> void delayBinding(PauseTransition pause,
                                        ObservableValue<T> property,
                                        ChangeListener<? super T> listener) {
        System.out.println(pause);
        property.addListener((prop, oldValue, newValue) -> {
            pause.setOnFinished(e -> listener.changed(prop, oldValue, newValue));
            pause.playFromStart();
        });
    }

    public static void setBackground(Region region, Color color) {
        if (color == null) {
            region.setBackground(null);
            return;
        }
        region.setBackground(new Background(new BackgroundFill(
                color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

}
