package game;

import csse2002.block.world.Position;
import javafx.scene.layout.Region;

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
}
