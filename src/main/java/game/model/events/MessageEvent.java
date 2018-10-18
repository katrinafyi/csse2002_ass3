package game.model.events;

/**
 * A non-error message event.
 */
public class MessageEvent extends BaseMessageEvent {
    /**
     * Construct a new message event.
     * @param message Message string.
     */
    public MessageEvent(String message) {
        super(message);
    }
}
