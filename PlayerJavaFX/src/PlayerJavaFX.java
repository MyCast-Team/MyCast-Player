import javafx.animation.AnimationTimer;
import javafx.application.Application;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

/**
 * Created by thomasfouan on 16/03/2016.
 */

public class PlayerJavaFX extends JavaFXDirectRendering {

    /**
     *
     */
    private final AnimationTimer timer;

    public PlayerJavaFX() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderFrame();
            }
        };
    }

    @Override
    protected void startTimer() {
        timer.start();
    }

    @Override
    protected void stopTimer() {
        timer.stop();
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        new NativeDiscovery().discover();
        Application.launch(args);
    }
}
