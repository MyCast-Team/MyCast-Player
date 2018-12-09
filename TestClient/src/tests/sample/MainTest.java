package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sample.controller.MainFrameController;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest extends ApplicationTest {

    private MainFrameController controller;

    @Override
    public void start(Stage stage) throws Exception {
        new NativeDiscovery().discover();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Main.PATH_TO_MAIN_VIEW));
        Parent mainNode = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @Before
    public void setUp () {
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void should_start_without_fail() {
        assertThat(controller).isNotNull();
    }
}