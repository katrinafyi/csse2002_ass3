package game.view.components;

import game.util.Cache;
import game.model.Direction;
import game.util.Utilities;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A widget for 4-directional input using buttons, arranged in a 3x3 grid.
 */
public class DPadGrid extends UniformGridPane {
    /**
     * Details of one specific button of the d-pad.
     */
    private static class ButtonSpec {
        /** Button's label. */
        public final String label;
        /** Grid column. */
        public final int col;
        /** Grid row. */
        public final int row;
        /** Direction this button represents. */
        public final Direction direction;

        /**
         * Constructs a new {@link ButtonSpec} with the given fields.
         * @param label Label.
         * @param col Grid column.
         * @param row Grid row.
         * @param direction Direction this button is for.
         */
        public ButtonSpec(String label, int col, int row, Direction direction) {
            this.label = label;
            this.col = col;
            this.row = row;
            this.direction = direction;
        }
    }

    /** Array of buttons to add. */
    private static final ButtonSpec[] buttonSpecs = new ButtonSpec[4];
    static {
        buttonSpecs[0] = new ButtonSpec("up.png", 1, 0, Direction.north);
        buttonSpecs[1] = new ButtonSpec("right.png", 2, 1, Direction.east);
        buttonSpecs[2] = new ButtonSpec("down.png", 1, 2, Direction.south);
        buttonSpecs[3] = new ButtonSpec("left.png", 0, 1, Direction.west);
    }
    /** Base path for all icon images. */
    private static final String ICON_PATH = "file:src/images/";

    /** Image to place in centre of directional buttons. */
    private ImageView centreImage;
    /** Buttons for each direction. */
    private final Map<Direction, Button> buttons = new HashMap<>();

    /**
     * Constructs a new {@link DPadGrid}, using the given callback as the
     * click handler for each button.
     * @param onClick Function taking a direction input.
     */
    public DPadGrid(Consumer<Direction> onClick) {
        super(3, 3);
        fixHeightToWidth();

        for (ButtonSpec buttonSpec : buttonSpecs) {
            Button button = new IconButton(ICON_PATH + buttonSpec.label);
            Utilities.setMaxWidthHeight(button);
            button.prefHeightProperty().bind(button.widthProperty());

            button.setOnAction(e -> onClick.accept(buttonSpec.direction));
            buttons.put(buttonSpec.direction, button);

            this.add(button, buttonSpec.col, buttonSpec.row);
        }
    }

    /**
     * Gets the ImageView for the centre image.
     * @return ImageView.
     */
    public ImageView getCentreImage() {
        return centreImage;
    }

    /**
     * Sets the centre image to the given image path.
     * @param url Path of image to use.
     */
    public void setCentreImage(String url) {
        Image image = Cache.getImageCache().get(url);
        centreImage = new ImageView(image);
        centreImage.setPreserveRatio(true);
        centreImage.setFitWidth(40);
        add(centreImage, 1, 1);
        GridPane.setHalignment(centreImage, HPos.CENTER);
        GridPane.setValignment(centreImage, VPos.CENTER);
    }

    /**
     * Returns the button associated with the given direction.
     * @param direction Direction of button to get.
     * @return Button.
     */
    public Button getButton(Direction direction) {
        return buttons.get(direction);
    }
}
