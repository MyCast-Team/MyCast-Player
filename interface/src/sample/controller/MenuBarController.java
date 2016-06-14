package sample.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import sample.model.ConnectionDialog;
import sample.model.StreamMedia;

import java.awt.*;
import java.util.Optional;

/**
 * Class of control of the menuBar.
 */
public class MenuBarController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu mediacase;
    @FXML
    private MenuItem openMedia;
    @FXML
    private MenuItem openMediaAndAdd;
    @FXML
    private Menu connection;
    @FXML
    private MenuItem setConnection;
    @FXML
    private MenuItem play;
    @FXML
    private MenuItem previous;
    @FXML
    private MenuItem next;

    private StreamMedia streamMedia;
    private static final String PATH_TO_VIDEO = "/Users/thomasfouan/Desktop/video.avi";

    public MenuBarController(){
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

        play.setDisable(true);
        previous.setDisable(true);
        next.setDisable(true);
    }

    /**
     * Return an EventHandler for the connection button.
     * Show a window to set the connection with a client or disconnect with client if already connected.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getConnectionEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia.getStatus() == StreamMedia.CONNECTION_STATUS.CONNECTED) {
                    if(!streamMedia.getSocket().isClosed()) {
                        streamMedia.closeConnection();
                    }
                    setConnection.setText("Set a new connection");
                    play.setDisable(true);
                    previous.setDisable(true);
                    next.setDisable(true);
                } else {
                    ConnectionDialog connectionDialog = new ConnectionDialog();
                    Optional<Pair<String, Integer>> result = connectionDialog.getDialog().showAndWait();
                    if (result.isPresent()) {
                        String addr = result.get().getKey();
                        int port = result.get().getValue();

                        System.out.println("Adresse : " + addr);
                        System.out.println("Port : " + port);

                        if(streamMedia.setClientConnection(addr, port)) {
                            setConnection.setText("Disconnect from connection");
                            play.setDisable(false);
                            previous.setDisable(false);
                            next.setDisable(false);
                        }
                    } else {
                        System.out.println("Canceled");
                    }
                }
            }
        };
    }

    /**
     * Return an EventHandler for the play/pause button.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getPlayEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                    if(streamMedia.getMediaListPlayer().isPlaying()) {
                        streamMedia.pauseStreamingMedia();
                        play.setText("Play");
                    } else {
                        streamMedia.startStreamingMedia();
                        play.setText("Pause");
                    }
                }
            }
        };
    }

    /**
     * Return an EventHandler for the previous item button.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getPreviousEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                    streamMedia.getMediaListPlayer().playPrevious();
                }
            }
        };
    }

    /**
     * Return an EventHandler for the next item button.
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getNextEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                    streamMedia.getMediaListPlayer().playNext();
                }
            }
        };
    }
}
