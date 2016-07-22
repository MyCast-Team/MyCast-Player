package sample.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.model.InterfaceDialog;
import sample.model.PluginManager;
import sample.model.Point;
import sample.model.StreamMedia;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Class of control of the menuBar.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date = "10/03/2016", description = "This is the controller to manage the MenuBar. It defines functions to call on events.")
public class MenuBarController {

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
    @FXML
    private MenuItem documentation;

    private StreamMedia streamMedia;

    private static Label statusLabel;

    public MenuBarController() {
    }

    @FXML
    public void initialize() {
        streamMedia = new StreamMedia(setConnection);

        setConnection.setOnAction(getConnectionEventHandler());
        play.setOnAction(getPlayEventHandler());
        previous.setOnAction(getPreviousEventHandler());
        next.setOnAction(getNextEventHandler());
        interfaceConf.setOnAction(getInterfaceConfEventHandler());

        add.setOnAction(getAddEventHandler());
        documentation.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("How to develop a plugin ?");
            alert.setHeaderText("How to develop a plugin ?");
            String s ="A plugin in MyCast is a JavaFX Pane. You need to develop it and export it to a .jar. Your plugin must respect the following properties :\n" +
                    "       - be a jar file (.jar extension)\n" +
                    "       - contain the package name \"plugin\"\n" +
                    "       - contain a main view name \"mainPluginView.fxml\" inside the \"plugin\" package, and with an AnchorPane as root pane\n" +
                    "       - if you want to add a controller to your .fxml, add a tag fx:controller to your root pane and link it to your controller path";
            alert.setContentText(s);
            alert.showAndWait();
        });

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
     *
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getConnectionEventHandler() {
        return (event) -> {
            if(streamMedia.getStatus() == StreamMedia.CONNECTION_STATUS.CONNECTED) {
                streamMedia.closeConnection();
                updateStreamMenu(true, null);
            } else {
                if (streamMedia.setClientConnection()) {
                    String host = streamMedia.getSocket().getInetAddress().getCanonicalHostName();
                    updateStreamMenu(false, host);
                }
            }
        };
    }

    /**
     * Return an EventHandler for the Interface Configuration button.
     *
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getInterfaceConfEventHandler() {
        return (event) -> {
            InterfaceDialog interfaceDialog = new InterfaceDialog();
        };
    }

    /**
     * Return an EventHandler for the play/pause button.
     *
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getPlayEventHandler() {
        return (event) -> {
            if (streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                if (streamMedia.getMediaListPlayer().isPlaying() || streamMedia.getMediaListPlayer().getMediaList().size() == 0) {
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
     *
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getPreviousEventHandler() {
        return (event) -> {
            if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                if(streamMedia.getMediaListPlayer().isPlaying()) {
                    streamMedia.getMediaListPlayer().playPrevious();
                } else {
                    streamMedia.getMediaListPlayer().playPrevious();
                    play.setText("Pause");
                }
            }
        };
    }

    /**
     * Return an EventHandler for the next item button.
     *
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getNextEventHandler() {
        return (event) -> {
            if(streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                if(streamMedia.getMediaListPlayer().isPlaying()) {
                    streamMedia.getMediaListPlayer().playNext();
                } else {
                    streamMedia.getMediaListPlayer().playNext();
                    play.setText("Pause");
                }
            }
        };
    }

    /**
     * Update the menuBar after setting or closing a connection.
     */
    public void updateStreamMenu(boolean isReset, String host) {
        if(isReset) {
            setConnection.setText("Set a new connection");
            play.setText("Play");
            play.setDisable(true);
            previous.setDisable(true);
            next.setDisable(true);
            statusLabel.setText("Not connected");
        } else {
            setConnection.setText("Disconnect with "+host);
            play.setDisable(false);
            previous.setDisable(false);
            next.setDisable(false);
            statusLabel.setText("Connected with "+host);
        }
    }

    /**
     * Return an event handler for the upload button. It handles the upload of plugins from java app to the server
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getAddEventHandler() {
        return (event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            File file = chooser.showOpenDialog(add.getParentPopup().getScene().getWindow());

            if(PluginManager.checkPluginValidity(file, true)) {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                HttpPost httppost = new HttpPost(Constant.SERVER_ADDRESS + "/plugin");
                try {
                    MultipartEntity mpEntity = new MultipartEntity();
                    ContentBody cbFile = new FileBody(file);
                    ContentBody author = new StringBody("testname");
                    mpEntity.addPart("plugin", cbFile);
                    mpEntity.addPart("author", author);

                    httppost.setEntity(mpEntity);

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        resEntity.consumeContent();
                    }

                    httpclient.getConnectionManager().shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
