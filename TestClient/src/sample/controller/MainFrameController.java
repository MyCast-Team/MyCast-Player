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

    @FXML
    private ImageView artworkView;

    private ResizablePlayer resizablePlayer;

    private Thread threadConnections;

    @FXML
    public void initialize() {
        this.resizablePlayer = new ResizablePlayer(this.playerHolder, this.imageView, this.artworkView);

        this.artworkView.setPreserveRatio(true);
        this.artworkView.setSmooth(true);

        try {
            threadConnections = new ThreadConnection(resizablePlayer.getMediaPlayer(), playerHolder, imageView, artworkView);
            threadConnections.start();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public ResizablePlayer getResizablePlayer() {
        return resizablePlayer;
    }

    public Thread getThreadConnections() {
        return threadConnections;
    }
}
