package sample.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
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
     *
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getConnectionEventHandler() {
        return (event) -> {
            if (streamMedia.getStatus() == StreamMedia.CONNECTION_STATUS.CONNECTED) {
                if (!streamMedia.getSocket().isClosed()) {
                    streamMedia.closeConnection();
                }
                setConnection.setText("Set a new connection");
                play.setText("Play");
                play.setDisable(true);
                previous.setDisable(true);
                next.setDisable(true);
                statusLabel.setText("Not connected");
            } else {
                if (streamMedia.setClientConnection()) {
                    String host = streamMedia.getSocket().getInetAddress().getCanonicalHostName();
                    setConnection.setText("Disconnect with " + host);
                    play.setDisable(false);
                    previous.setDisable(false);
                    next.setDisable(false);
                    statusLabel.setText("Connected with " + host);
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
            if (streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                streamMedia.getMediaListPlayer().pause();
                streamMedia.getMediaListPlayer().playPrevious();
                streamMedia.getMediaListPlayer().play();
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
            if (streamMedia != null && streamMedia.getStatus().equals(StreamMedia.CONNECTION_STATUS.CONNECTED)) {
                streamMedia.getMediaListPlayer().pause();
                streamMedia.getMediaListPlayer().playNext();
                streamMedia.getMediaListPlayer().play();
            }
        };
    }

    private EventHandler<ActionEvent> getAddEventHandler() {
        return (event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            File file = chooser.showOpenDialog(add.getParentPopup().getScene().getWindow());
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost("http://localhost:3000/plugin");
            try {
            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody cbFile = new FileBody(file);
            ContentBody author = new StringBody("testname");
            mpEntity.addPart("plugin", cbFile);
            mpEntity.addPart("author",author);


                httppost.setEntity(mpEntity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();

                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    System.out.println(EntityUtils.toString(resEntity));
                }
                if (resEntity != null) {
                    resEntity.consumeContent();
                }

                httpclient.getConnectionManager().shutdown();
            }catch (Exception e){
                e.printStackTrace();
            }
            /*InputStream inStream;


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://localhost:3000/plugin");
            HttpResponse response;
            HttpEntity entity;

            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("author", "testname"));
            params.add(new BasicNameValuePair("originalname", file.getName()));
            params.add(new BasicNameValuePair("plugin",file));


            try {
                httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                //Execute and get the response.

                response = httpclient.execute(httppost);
                System.out.println(response.getEntity());


                entity = response.getEntity();
                if (entity != null) {
                    inStream = entity.getContent();
                    inStream.close();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        };
    }

}
