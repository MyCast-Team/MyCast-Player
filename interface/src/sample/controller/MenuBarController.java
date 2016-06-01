package sample.controller;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    @FXML
    private Menu plugin;
    @FXML
    private MenuItem add;
    @FXML
    private MenuItem remove;
    @FXML
    private MenuItem download;
    private StreamMedia streamMedia;
    private static final String PATH_TO_VIDEO = "/Users/thomasfouan/Desktop/video.avi";

    public MenuBarController(){
    }

    @FXML
    public void initialize(){
        setConnection.setOnAction(getConnectionEventHandler());
        play.setOnAction(getPlayEventHandler());
        pause.setOnAction(getPauseEventHandler());
        add.setOnAction(getAddEventHandler());
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
