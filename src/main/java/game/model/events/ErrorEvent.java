package game.model.events;

public class ErrorEvent extends BaseMessageEvent {
    public ErrorEvent(String message) {
        super(message);
    }
}
