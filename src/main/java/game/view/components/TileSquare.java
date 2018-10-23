package game.view.components;

import csse2002.block.world.Tile;
import game.model.BlockType;
import game.model.Direction;
import game.util.Cache;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Visual representation of a single {@link Tile} on the map.
 */
public class TileSquare extends StackPane implements TileView {
    /** Mapping of block types to images to use. null is for no blocks. */
    private final static Map<BlockType, String> blockImages = new HashMap<>();
    static {
        blockImages.put(null, "file:src/images/bedrock.png");
        blockImages.put(BlockType.grass, ("file:src/images/grass_top2.png"));
        blockImages.put(BlockType.soil, ("file:src/images/dirt.png"));
        blockImages.put(BlockType.stone, ("file:src/images/stone.png"));
        blockImages.put(BlockType.wood, ("file:src/images/oak_planks.png"));
    }

    /** ImageView of the top block. */
    private final ImageView blockImage;
    /** ImageView of the height number. */
    private final ImageView heightImage;
    /** Overlay of the tile's exits. */
    private final ExitsOverlay exitsOverlay;
    /** ImageView of the builder head icon if the builder is on this tile. */
    private ImageView steveImage;
    /** Overlay of the ambient occlusion shading. */
    private final AmbientOcclusion ambientOcclusion;

    /**
     * Construct a new blank {@link TileSquare}.
     */
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

        this.getChildren().addAll(blockImage, heightImage, exitsOverlay,
                ambientOcclusion);
        setCacheOptions(getChildren());
    }

    /**
     * Enables fast caching of all nodes in the given list.
     * @param nodes Collection of nodes to cache.
     */
    private void setCacheOptions(Collection<Node> nodes) {
        for (Node node : nodes) {
            node.setCache(true);
            node.setCacheHint(CacheHint.SPEED);
        }
    }

    /**
     * Returns a new {@link ImageView} with {@link ImageView#preserveRatio}
     * set to true.
     * @return ImageView.
     */
    private static ImageView ratioImageView() {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Gets the given image URl from the global cache and applies it to the
     * given imageView. If image is null, will remove the current image.
     * @param imageView ImageView to set image onto.
     * @param url URL of image to set.
     */
    private static void loadAndSetImage(ImageView imageView, String url) {
        Image image = url == null
                ? null : Cache.getImageCache().get(url);
        imageView.setImage(image);
    }

    /**
     * Sets whether this tile currently contains the builder or not.
     * @param isBuilderTile True if the builder is on the tile, false otherwise.
     */
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

    /**
     * Sets the height of this tile to the given height.
     * @param height Number of blocks on the tile.
     */
    @Override
    public void setHeight(int height) {
        String path = "file:src/images/"+height+".png";
        loadAndSetImage(heightImage, path);
    }

    /**
     * Sets whether this tile has an exit in the given direction.
     * @param direction Exit direction.
     * @param hasExit Presence of exit.
     */
    @Override
    public void setHasExit(Direction direction, boolean hasExit) {
        exitsOverlay.setHasExit(direction, hasExit);
    }

    /**
     * Sets the block type of the top block on this tile, or null if there are
     * no blocks.
     * @param blockType Top block type, or null for no blocks.
     */
    @Override
    public void setTopBlock(BlockType blockType) {
        String path = blockImages.get(blockType);
        loadAndSetImage(blockImage, path);
    }

    /**
     * Sets whether exits are to be displayed.
     * @param visible True if exits are to be visible.
     */
    @Override
    public void setExitVisibility(boolean visible) {
        exitsOverlay.setVisible(visible);
    }

    /**
     * Sets whether heights are to be displayed.
     * @param visible True if heights are to be visible.
     */
    @Override
    public void setHeightVisibility(boolean visible) {
        heightImage.setVisible(visible);
    }

    /**
     * Gets the ambient occlusion overlay of tile tile.
     */
    public AmbientOcclusion getAmbientOcclusion() {
        return ambientOcclusion;
    }

}
