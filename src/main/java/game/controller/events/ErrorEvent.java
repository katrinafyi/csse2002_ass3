package game.controller.events;

public class ErrorEvent extends BaseBlockWorldEvent {
    private final String message;

    public ErrorEvent(String message) {

        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
