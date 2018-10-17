package game.view.components;

import game.view.SpriteLoader;
import game.Utilities;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends Button {
    public IconButton(String imageUrl) {
        this(imageUrl, 40);
    }

    public IconButton(String imageUrl, double width) {
        Utilities.setMaxWidthHeight(this);

        Image image = SpriteLoader.getGlobalLoader().loadImage(imageUrl);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);

        this.setGraphic(imageView);
        this.setPadding(new Insets(3));
    }
}
