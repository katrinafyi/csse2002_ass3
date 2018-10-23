package game.view.components;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Label widget for a temporary message. Can be shown with some text, then
 * fades after a duration.
 */
public class FadingLabel extends Label {
    /** Pause transition to wait for. */
    private final PauseTransition pauseTransition;
    /** Fade transition describing fade. */
    private final FadeTransition fadeTransition;

    /**
     * Construct a new {@link FadingLabel} which stays visible for the duration
     * of {@code visible}, then fades to transparent over the duraction of
     * {@code fade}.
     * @param visible Visible duration.
     * @param fade Fade duration.
     */
    public FadingLabel(Duration visible, Duration fade) {
        setOpacity(0); // Start invisible.

        pauseTransition = new PauseTransition(visible);
        pauseTransition.setOnFinished(this::beginFade);

        fadeTransition = new FadeTransition(fade, this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
    }

    /**
     * Finished handler for the pause transition. Starts the fade transition.
     * @param e Event.
     */
    private void beginFade(ActionEvent e) {
        fadeTransition.playFromStart();
    }

    /**
     * Show the given message, then fade as appropriate.
     * @param message Message text.
     */
    public void showAndFade(String message) {
        setText(message);
        setOpacity(1.0);
        fadeTransition.stop(); // Stop any current fade.
        pauseTransition.playFromStart();
    }
}
