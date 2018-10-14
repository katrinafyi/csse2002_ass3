package game.view;

import game.model.Direction;

public interface BuilderControlsView {
    void updateCanMoveBuilder(Direction direction, boolean canMove);
    void upadteCanMoveBlock(Direction direction, boolean canMode);
    void updateCanDig(boolean canDig);
}
