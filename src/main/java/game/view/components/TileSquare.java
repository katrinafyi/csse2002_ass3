package game.view.components;

import game.model.BlockType;
import game.model.Direction;
import game.util.Cache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;


public class TileSquare extends StackPane implements TileView {
    private final static Map<BlockType, String> blockImages = new HashMap<>();
    static {
        blockImages.put(null, "file:src/images/bedrock.png");
        blockImages.put(BlockType.grass, ("file:src/images/grass_top2.png"));
        blockImages.put(BlockType.soil, ("file:src/images/dirt.png"));
        blockImages.put(BlockType.stone, ("file:src/images/stone.png"));
        blockImages.put(BlockType.wood, ("file:src/images/oak_planks.png"));
    }

    private final ImageView blockImage;
    private final ImageView heightImage;
    private final ExitsOverlay exitsOverlay;
    private ImageView steveImage;
    private final AmbientOcclusion ambientOcclusion;

    public TileSquare() {
        this.setAlignment(Pos.CENTER);

        exitsOverlay = new ExitsOverlay();
        exitsOverlay.prefWidthProperty().bind(this.maxWidthProperty());

        blockImage = ratioImageView();
        blockImage.fitWidthProperty().bind(this.maxWidthProperty());

        heightImage = ratioImageView();
        heightImage.fitWidthProperty().bind(this.maxWidthProperty().divide(5));
        StackPane.setAlignment(heightImage, Pos.TOP_LEFT);
        StackPane.setMargin(heightImage, new Insets(5));

        ambientOcclusion = new AmbientOcclusion();
        ambientOcclusion.maxWidthProperty().bind(this.maxWidthProperty());

        this.getChildren().addAll(
                blockImage, heightImage,
                exitsOverlay, ambientOcclusion);
    }

    private static ImageView ratioImageView() {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private static void loadAndSetImage(ImageView imageView, String url) {
        Image image = url == null
                ? null : Cache.getImageCache().get(url);
        imageView.setImage(image);
    }

    @Override
    public void setBuilderTile(boolean isBuilderTile) {
        if (steveImage != null) {
            steveImage.setVisible(isBuilderTile);
            return;
        }
        if (isBuilderTile) {
            steveImage = ratioImageView();
            loadAndSetImage(steveImage, "file:src/images/steve_shadow.png");
            steveImage.fitWidthProperty().bind(this.maxWidthProperty().multiply(0.8));
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
        exitsOverlay.setHasExit(direction, hasExit);
    }

    @Override
    public void setTopBlock(BlockType blockType) {
        String path = blockImages.get(blockType);
        loadAndSetImage(blockImage, path);
    }

    @Override
    public void setExitVisibility(boolean visible) {
        exitsOverlay.setVisible(visible);
    }

    @Override
    public void setHeightVisibility(boolean visible) {
        heightImage.setVisible(visible);
    }

    public AmbientOcclusion getAmbientOcclusion() {
        return ambientOcclusion;
    }

}
