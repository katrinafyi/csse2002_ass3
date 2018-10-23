package game.model.events;

/**
 * Represents a generic error event.
 */
public class ErrorEvent extends BaseMessageEvent {
    /**
     * Construct a new {@link ErrorEvent} with the given message.
     * @param message Error message.
     */
    public ErrorEvent(String message) {
        super(message);
    }
}
