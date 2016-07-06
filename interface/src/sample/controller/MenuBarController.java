package sample.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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

    public MenuBarController() {
    }

    @FXML
    public void initialize() {
        streamMedia = new StreamMedia();

        setConnection.setOnAction(getConnectionEventHandler());
        play.setOnAction(getPlayEventHandler());
        previous.setOnAction(getPreviousEventHandler());
        next.setOnAction(getNextEventHandler());
        interfaceConf.setOnAction(getInterfaceConfEventHandler());

        play.setDisable(true);
        previous.setDisable(true);
        next.setDisable(true);
    }

    public StreamMedia getStreamMedia() {
        return streamMedia;
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
                play.setText("Play");
                play.setDisable(true);
                previous.setDisable(true);
                next.setDisable(true);
                statusLabel.setText("Not connected");
            } else {
                if(streamMedia.setClientConnection()) {
                    String host = streamMedia.getSocket().getInetAddress().getCanonicalHostName();
                    setConnection.setText("Disconnect with "+host);
                    play.setDisable(false);
                    previous.setDisable(false);
                    next.setDisable(false);
                    statusLabel.setText("Connected with "+host);
                }
            }
        };
    }

    /**
     * Return an EventHandler for the Interface Configuration button.
     * @return EventHandler
     */
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
                if(streamMedia.getMediaListPlayer().isPlaying() || streamMedia.getMediaListPlayer().getMediaList().size() == 0) {
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
                streamMedia.getMediaListPlayer().pause();
                streamMedia.getMediaListPlayer().playPrevious();
                streamMedia.getMediaListPlayer().play();
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
                streamMedia.getMediaListPlayer().pause();
                streamMedia.getMediaListPlayer().playNext();
                streamMedia.getMediaListPlayer().play();
            }
        };
    }
}
