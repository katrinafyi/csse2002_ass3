package game.view;

import game.model.Direction;

public interface BuilderControlsView {
    void updateCanMoveBuilder(Direction direction, boolean canMove);
    void updateCanDig(boolean canDig);
}
