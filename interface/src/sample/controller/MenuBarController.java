package sample.controller;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
<<<<<<< HEAD
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
=======
>>>>>>> f1480a5ae66437a3e7ba8b3fb22a0d69e9e7bfaa
import javafx.util.Pair;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import sample.model.ConnectionDialog;
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
<<<<<<< HEAD
    private MenuItem pause;
    @FXML
    private Menu plugin;
    @FXML
    private MenuItem add;
    @FXML
    private MenuItem remove;
    @FXML
    private MenuItem download;
=======
    private MenuItem previous;
    @FXML
    private MenuItem next;
    @FXML
    private MenuItem interfaceConf;

>>>>>>> f1480a5ae66437a3e7ba8b3fb22a0d69e9e7bfaa
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
<<<<<<< HEAD
        pause.setOnAction(getPauseEventHandler());
        add.setOnAction(getAddEventHandler());
        streamMedia = new StreamMedia();
=======
        previous.setOnAction(getPreviousEventHandler());
        next.setOnAction(getNextEventHandler());
        interfaceConf.setOnAction(getInterfaceConfEventHandler());

        play.setDisable(true);
        previous.setDisable(true);
        next.setDisable(true);
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
>>>>>>> f1480a5ae66437a3e7ba8b3fb22a0d69e9e7bfaa
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

    private EventHandler<ActionEvent> getAddEventHandler() {
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Open File");

                File file=chooser.showOpenDialog(add.getParentPopup().getScene().getWindow());
                System.out.println(file.getAbsolutePath());
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
