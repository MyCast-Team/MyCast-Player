package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sample.model.ResizablePlayer;
import sample.model.ThreadConnection;

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

    private Thread threadConnections;

    @FXML
    public void initialize() {
        this.resizablePlayer = new ResizablePlayer(this.playerHolder, this.imageView);

        try {
            threadConnections = new ThreadConnection(resizablePlayer.getMediaPlayer(), playerHolder);
            threadConnections.start();
        } catch (IOException e) {
            System.out.println("Impossible to create the server socket.");
            e.printStackTrace();
            threadConnections = null;
            resizablePlayer.release();
        }
    }

    public ResizablePlayer getResizablePlayer() {
        return resizablePlayer;
    }

    public Thread getThreadConnections() {
        return threadConnections;
    }
}
