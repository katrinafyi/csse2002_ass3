package game.controller;

/**
 * Controller for error handling. Separate from block world interactions.
 */
public interface ErrorController {
    /**
     * Handle an error with the given message.
     * @param errorMessage Error message.
     */
    void handleError(String errorMessage);
}
