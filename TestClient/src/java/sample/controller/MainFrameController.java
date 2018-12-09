package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sample.model.*;

import java.io.IOException;

/**
 * Created by thomasfouan on 18/07/2016.
 */
public class MainFrameController {

    private static final int SERVER_PORT = 12345;

    @FXML
    private Pane playerHolder;

    @FXML
    private ImageView imageView;

    private ResizablePlayer resizablePlayer;

    private ThreadConnection threadConnection;

    public MainFrameController() {
    }

    MainFrameController(ResizablePlayer player, ThreadConnection threadConnection) {
        resizablePlayer = player;
        this.threadConnection = threadConnection;

        startThreadConnection();
    }

    @FXML
    public void initialize() {
        resizablePlayer = new ResizablePlayer(playerHolder, imageView);

        try {
            threadConnection = new ThreadConnection(
                    new ConnectionHandler(
                            resizablePlayer,
                            new ServerSocketService(SERVER_PORT),
                            new SystemOutputPrinterService()
                    )
            );
        } catch (IOException e) {
            System.out.println("Impossible to create the server socket.");
            e.printStackTrace();
            threadConnection = null;
            resizablePlayer.release();
        }

        startThreadConnection();
    }

    private void startThreadConnection() {
        threadConnection.start();
    }

    public void release() throws InterruptedException {
        threadConnection.stop();
        resizablePlayer.release();
    }

}
