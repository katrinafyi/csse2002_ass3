package game.model.events;

public abstract class BaseMessageEvent extends BaseBlockWorldEvent {
    private final String message;

    public BaseMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
