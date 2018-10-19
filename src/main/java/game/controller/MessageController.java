package game.controller;

/**
 * Controller for message handling. Separate from block world interactions.
 */
public interface MessageController {
    /**
     * Handle an error with the given message.
     * @param errorMessage Error message.
     */
    void handleErrorMessage(String errorMessage);

    /**
     * Handle an informational message with the given message.
     * @param message Message text.
     */
    void handleInfoMessage(String message);
}
