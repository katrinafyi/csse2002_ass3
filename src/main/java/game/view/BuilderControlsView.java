package game.view;

public interface BuilderControlsView {
    void setCanMoveBuilder(Direction direction, boolean canMove);
    void setCanDig(boolean canDig);
    void setCanMoveBlock(Direction direction, boolean canMode);
}
