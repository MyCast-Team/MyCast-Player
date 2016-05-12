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
    private MenuItem pause;
    private StreamMedia streamMedia;
    private static final String PATH_TO_VIDEO = "/Users/thomasfouan/Desktop/video.avi";

    public MenuBarController(){
    }

    @FXML
    public void initialize(){
        setConnection.setOnAction(getConnectionEventHandler());
        play.setOnAction(getPlayEventHandler());
        pause.setOnAction(getPauseEventHandler());
        streamMedia = new StreamMedia();
    }

    private EventHandler<ActionEvent> getConnectionEventHandler() {
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia.getStatus() == StreamMedia.CONNECTION_STATUS.CONNECTED) {
                    if(!streamMedia.getSocket().isClosed()) {
                        streamMedia.closeConnection();
                    }
                    setConnection.setText("Set a new connection");
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
                            streamMedia.getPlayList().addMedia(PATH_TO_VIDEO);
                        }
                    } else {
                        System.out.println("Canceled");
                    }
                }
            }
        };

        return handler;
    }

    private EventHandler<ActionEvent> getPlayEventHandler() {
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                    streamMedia.startStreamingMedia();
                }
            }
        };

        return handler;
    }

    private EventHandler<ActionEvent> getPauseEventHandler() {
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                    streamMedia.pauseStreamingMedia();
                }
            }
        };

        return handler;
    }
}
