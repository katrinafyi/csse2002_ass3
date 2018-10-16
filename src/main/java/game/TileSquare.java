package game;

import game.model.BlockType;
import game.model.Direction;
import game.view.TileView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;


public class TileSquare extends StackPane implements TileView {
    private final static Map<BlockType, Image> blockImages = new HashMap<>();
    static {
        blockImages.put(BlockType.grass,
                new Image(TileSquare.class.getResourceAsStream("/grass_top2.png")));
        blockImages.put(BlockType.soil,
                new Image(TileSquare.class.getResourceAsStream("/dirt.png")));
        blockImages.put(BlockType.stone,
                new Image(TileSquare.class.getResourceAsStream("/stone.png")));
        blockImages.put(BlockType.wood,
                new Image(TileSquare.class.getResourceAsStream("/oak_planks.png")));
    }

    private final ImageView imageView = new ImageView();

    public TileSquare() {
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.maxWidthProperty());
        this.getChildren().add(imageView);
    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public void setHasExit(Direction direction, boolean hasExit) {

    }

    @Override
    public void setTopBlock(BlockType blockType) {
        imageView.setImage(blockImages.get(blockType));
    }

    @Override
    public void setTooHighOrLow(boolean tooHighOrLow) {

    }
}
