package game.view.components;

import game.view.SpriteLoader;
import game.view.UniformGridPane;
import game.model.Direction;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DPadGrid extends UniformGridPane {
    private static class ButtonSpec {
        public final String label;
        public final int col;
        public final int row;
        public final Direction direction;

        public ButtonSpec(String label, int col, int row, Direction direction) {
            this.label = label;
            this.col = col;
            this.row = row;
            this.direction = direction;
        }
    }

    private static final ButtonSpec[] buttonSpecs = new ButtonSpec[4];
    static {
        buttonSpecs[0] = new ButtonSpec("up.png", 1, 0, Direction.north);
        buttonSpecs[1] = new ButtonSpec("right.png", 2, 1, Direction.east);
        buttonSpecs[2] = new ButtonSpec("down.png", 1, 2, Direction.south);
        buttonSpecs[3] = new ButtonSpec("left.png", 0, 1, Direction.west);
    }
    private static final String ICON_PATH = "file:src/images/";

    private ImageView centreImage;
    private final Map<Direction, Button> buttons = new HashMap<>();

    public DPadGrid(Consumer<Direction> onClick) {
        super(3, 3);
        fixHeightToWidth();

        for (ButtonSpec buttonSpec : buttonSpecs) {
            Button button = makeButton(buttonSpec);
            button.setOnAction(e -> onClick.accept(buttonSpec.direction));
            buttons.put(buttonSpec.direction, button);

            this.add(button, buttonSpec.col, buttonSpec.row);
        }
    }

    public ImageView getCentreImage() {
        return centreImage;
    }

    public void setCentreImage(String url) {
        Image image = SpriteLoader.getGlobalLoader().loadImage(url);
        centreImage = new ImageView(image);
        centreImage.setPreserveRatio(true);
        centreImage.setFitWidth(40);
        centreImage.setOpacity(0.5);
        add(centreImage, 1, 1);
        GridPane.setHalignment(centreImage, HPos.CENTER);
        GridPane.setValignment(centreImage, VPos.CENTER);
    }

    private Button makeButton(ButtonSpec buttonSpec) {
        Button button = new IconButton(ICON_PATH + buttonSpec.label);
        button.setDisable(true);
        button.prefHeightProperty().bind(button.widthProperty());

        return button;
    }

    public Button getButton(Direction direction) {
        return buttons.get(direction);
    }
}
