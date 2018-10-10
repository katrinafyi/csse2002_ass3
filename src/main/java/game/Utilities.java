package game;

import javafx.scene.layout.Region;

public class Utilities {
    public static void setMaxWidthHeight(Region region) {
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMaxHeight(Double.MAX_VALUE);
    }
}
