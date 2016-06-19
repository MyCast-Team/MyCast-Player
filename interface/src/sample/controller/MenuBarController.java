package sample.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.util.Pair;
import sample.model.ConnectionDialog;
import sample.model.InterfaceDialog;
import sample.model.StreamMedia;

import java.util.Optional;

/**
 * Class of control of the menuBar.
 */
public class MenuBarController {

    @FXML
    private MenuItem openMedia;
    @FXML
    private MenuItem openMediaAndAdd;
    @FXML
    private MenuItem setConnection;
    @FXML
    private MenuItem play;
    @FXML
    private MenuItem previous;
    @FXML
    private MenuItem next;
    @FXML
    private MenuItem interfaceConf;

    private StreamMedia streamMedia;

    private Label statusLabel;

    private static final String PATH_TO_VIDEO = "/Users/thomasfouan/Desktop/video.avi";

    public MenuBarController() {
    }

    @FXML
    public void initialize() {
        streamMedia = new StreamMedia();
        streamMedia.getPlayList().addMedia(PATH_TO_VIDEO);
        streamMedia.getPlayList().addMedia("/Users/thomasfouan/Desktop/music.mp3");

        setConnection.setOnAction(getConnectionEventHandler());
        play.setOnAction(getPlayEventHandler());
        previous.setOnAction(getPreviousEventHandler());
        next.setOnAction(getNextEventHandler());
        interfaceConf.setOnAction(getInterfaceConfEventHandler());

        play.setDisable(true);
        previous.setDisable(true);
        next.setDisable(true);
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    /**
     * Return an EventHandler for the connection button.
     * Show a window to set the connection with a client or disconnect with client if already connected.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getConnectionEventHandler() {
        return (event) -> {
            if(streamMedia.getStatus() == StreamMedia.CONNECTION_STATUS.CONNECTED) {
                if(!streamMedia.getSocket().isClosed()) {
                    streamMedia.closeConnection();
                }
                setConnection.setText("Set a new connection");
                play.setDisable(true);
                previous.setDisable(true);
                next.setDisable(true);
                statusLabel.setText("Not connected");
            } else {
                ConnectionDialog connectionDialog = new ConnectionDialog();
                Optional<Pair<String, Integer>> result = connectionDialog.getDialog().showAndWait();
                if (result.isPresent()) {
                    String addr = result.get().getKey();
                    int port = result.get().getValue();

                    if(streamMedia.setClientConnection(addr, port)) {
                        setConnection.setText("Disconnect from connection");
                        play.setDisable(false);
                        previous.setDisable(false);
                        next.setDisable(false);
                        statusLabel.setText("Connected with "+streamMedia.getSocket().getInetAddress().getCanonicalHostName());
                    }
                } else {
                    System.out.println("Canceled");
                }
            }
        };
    }

    private EventHandler<ActionEvent> getInterfaceConfEventHandler() {
        return (event) -> {
            InterfaceDialog interfaceDialog = new InterfaceDialog();
        };
    }

    /**
     * Return an EventHandler for the play/pause button.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getPlayEventHandler() {
        return (event) -> {
            if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                if(streamMedia.getMediaListPlayer().isPlaying()) {
                    streamMedia.pauseStreamingMedia();
                    play.setText("Play");
                } else {
                    streamMedia.startStreamingMedia();
                    play.setText("Pause");
                }
            }
        };
    }

    /**
     * Return an EventHandler for the previous item button.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getPreviousEventHandler() {
        return (event) -> {
            if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                streamMedia.getMediaListPlayer().playPrevious();
            }
        };
    }

    /**
     * Return an EventHandler for the next item button.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getNextEventHandler() {
        return (event) -> {
            if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                streamMedia.getMediaListPlayer().playNext();
            }
        };
    }
}
