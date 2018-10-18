package game.controller;

/**
 * Controller for message handling. Separate from block world interactions.
 */
public interface MessageController {
    /**
     * Handle an error with the given message.
     * @param errorMessage Error message.
     */
    void handleError(String errorMessage);

    /**
     * Handle a non-error message with the given message.
     * @param message Message text.
     */
    void handleMessage(String message);
}
