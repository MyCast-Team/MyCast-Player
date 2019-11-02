package sample;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sample.config.Config;
import sample.config.PropertiesLoader;
import sample.connection.ConnectionHandler;
import sample.connection.ThreadConnection;
import sample.connection.socket.ServerSocketService;
import sample.output.SystemOutputPrinterService;
import sample.player.ResizablePlayer;

import java.io.IOException;

/**
 * Created by thomasfouan on 18/07/2016.
 */
public class MainFrameController {

    @FXML
    private Pane playerHolder;

    @FXML
    private ImageView imageView;

    private ResizablePlayer resizablePlayer;

    private ThreadConnection threadConnection;

    public MainFrameController() {
    }

    MainFrameController(ResizablePlayer resizablePlayer, ThreadConnection threadConnection) {
        this.resizablePlayer = resizablePlayer;
        this.threadConnection = threadConnection;

        startThreadConnection();
    }

    @FXML
    public void initialize() throws IOException {
        Config config = PropertiesLoader.from("/config.yml").load();

        resizablePlayer = new ResizablePlayer(playerHolder, imageView);
        threadConnection = new ThreadConnection(
                new ConnectionHandler(
                        resizablePlayer,
                        new ServerSocketService(config.ports.server),
                        new SystemOutputPrinterService(),
                        config.ports.streaming
                )
        );

        startThreadConnection();
    }

    private void startThreadConnection() {
        threadConnection.start();
    }

    void release() throws InterruptedException {
        threadConnection.stop();
        resizablePlayer.release();
    }

}
