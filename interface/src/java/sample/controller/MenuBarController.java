package sample.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import sample.model.InterfaceDialog;
import sample.model.StreamMedia;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
    @FXML
    private MenuItem add;
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
        add.setOnAction(getAddEventHandler());
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
    private EventHandler<ActionEvent> getAddEventHandler() {
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Open File");

                File file=chooser.showOpenDialog(add.getParentPopup().getScene().getWindow());

                HttpClient httpclient = new DefaultHttpClient();


                HttpPost httppost = new HttpPost("http://localhost:3000/plugin");
                List<NameValuePair> params = new ArrayList<NameValuePair>(2);
                params.add(new BasicNameValuePair("author", "testname"));
                params.add(new BasicNameValuePair("originalname", file.getName()));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //Execute and get the response.
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                    System.out.println(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream instream = null;
                    try {
                        instream = entity.getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // do something useful
                    } finally {
                        try {
                            instream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        return handler;
    }
}