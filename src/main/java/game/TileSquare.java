package game;

import game.model.BlockType;
import game.model.Direction;
import game.view.TileView;
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

    private final ImageView blockImage = new ImageView();

    public TileSquare() {
        this.setStyle("-fx-background-color: black;");
        blockImage.setPreserveRatio(true);
        blockImage.fitWidthProperty().bind(this.maxWidthProperty());
        this.getChildren().add(blockImage);
    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public void setHasExit(Direction direction, boolean hasExit) {

    }

    @Override
    public void setTopBlock(BlockType blockType) {
        String path = blockImages.get(blockType);
        blockImage.setImage(SpriteLoader.getGlobalLoader().loadImage(path));
    }

    @Override
    public void setTooHighOrLow(boolean tooHighOrLow) {

    }
}
