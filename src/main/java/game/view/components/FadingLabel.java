package game.view.components;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class FadingLabel extends Label {

    private final PauseTransition pauseTransition;
    private final FadeTransition fadeTransition;

    public FadingLabel(Duration visible, Duration fade) {
        setOpacity(0);

        pauseTransition = new PauseTransition(visible);
        pauseTransition.setOnFinished(this::beginFade);

        fadeTransition = new FadeTransition(fade, this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
    }

    private void beginFade(ActionEvent e) {
        fadeTransition.playFromStart();
    }

    public void showAndFade(String message) {
        this.setText(message);
        this.setOpacity(1.0);
        pauseTransition.playFromStart();
        fadeTransition.stop();
    }
}
