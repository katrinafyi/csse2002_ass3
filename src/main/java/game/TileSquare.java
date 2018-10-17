package game;

import game.model.BlockType;
import game.model.Direction;
import game.view.TileView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;


public class TileSquare extends StackPane implements TileView {
    private final static Map<BlockType, String> blockImages = new HashMap<>();
    static {
        blockImages.put(BlockType.grass, ("file:src/images/grass_top2.png"));
        blockImages.put(BlockType.soil, ("file:src/images/dirt.png"));
        blockImages.put(BlockType.stone, ("file:src/images/stone.png"));
        blockImages.put(BlockType.wood, ("file:src/images/oak_planks.png"));
    }

    private final ImageView blockImage;
    private final ImageView heightImage;
    private ImageView steveImage;

    public TileSquare() {
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: black;");

        blockImage = squareImageView();
        blockImage.fitWidthProperty().bind(this.maxWidthProperty());

        heightImage = squareImageView();
        heightImage.fitWidthProperty().bind(this.maxWidthProperty().divide(4));
        StackPane.setAlignment(heightImage, Pos.TOP_LEFT);
        StackPane.setMargin(heightImage, new Insets(5));

        this.getChildren().addAll(blockImage, heightImage);
    }

    private static ImageView squareImageView() {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private static void loadAndSetImage(ImageView imageView, String url) {
        imageView.setImage(SpriteLoader.getGlobalLoader().loadImage(url));
    }

    @Override
    public void setBuilderTile(boolean isBuilderTile) {
        if (isBuilderTile && steveImage == null) {
            steveImage = squareImageView();
            loadAndSetImage(steveImage, "file:src/images/steve.png");
            steveImage.fitWidthProperty().bind(this.maxWidthProperty().divide(2));
            this.getChildren().add(steveImage);
        }
    }

    @Override
    public void setHeight(int height) {
        String path = "file:src/images/"+height+".png";
        loadAndSetImage(heightImage, path);
    }

    @Override
    public void setHasExit(Direction direction, boolean hasExit) {

    }

    @Override
    public void setTopBlock(BlockType blockType) {
        String path = blockImages.get(blockType);
        loadAndSetImage(blockImage, path);
    }

    @Override
    public void setTooHighOrLow(boolean tooHighOrLow) {

    }
}
