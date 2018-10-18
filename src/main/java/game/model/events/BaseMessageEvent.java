package game.model.events;

/**
 * Superclass for all messaging events.
 */
public abstract class BaseMessageEvent extends BaseBlockWorldEvent {
    private final String message;

    /**
     * Create a new event with the given message.
     * @param message Message.
     */
    public BaseMessageEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the event's message.
     * @return Message string.
     */
    public String getMessage() {
        return message;
    }
}
