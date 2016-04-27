import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;
import sun.applet.Main;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Created by thomasfouan on 26/04/2016.
 */
public class MyApp extends Application {

    private static final String PATH_TO_VIDEO = "/Users/thomasfouan/Desktop/video.avi";
    // new RtspMrl().host("127.0.0.1").port(12345).path("/demo").value();
    private static final String PATH_TO_MUSIC = "/Users/thomasfouan/Desktop/music.mp3";

    private ResizablePlayer resizablePlayer;

    private DirectMediaPlayerComponent mediaPlayerComponent;

    //private Pane playerHolder;

    private PlayerController playerController;

    private CONNECTION_STATUS status = CONNECTION_STATUS.DISCONNECTED;

    private Socket socket;

    private PrintWriter sendData;

    private StreamMedia streamMedia;

    /**
     * Create the control bar of the player.
     * @param stage of the screen
     * @return HBox made of multiple buttons, sliders, and labels
     */
    /*
    private HBox getControlBar(Stage stage) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        // previous button creation, with its controller
        Button previous = new Button();
        previous.setGraphic(new ImageView(new Image("./img/previous.png")));
        previous.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediaPlayerComponent.getMediaPlayer().previousChapter();
            }
        });

        // play/pause button creation, with its controller
        Button play = new Button();
        play.setGraphic(new ImageView(new Image("./img/play.png")));
        play.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayerComponent.getMediaPlayer().isPlaying()) {
                    mediaPlayerComponent.getMediaPlayer().pause();
                    play.setGraphic(new ImageView(new Image("./img/pause.png")));
                } else {
                    mediaPlayerComponent.getMediaPlayer().play();
                    play.setGraphic(new ImageView(new Image("./img/play.png")));
                }
            }
        });

        // stop button creation, with its controller
        Button stop = new Button();
        stop.setGraphic(new ImageView(new Image("./img/stop.png")));
        stop.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayerComponent.getMediaPlayer().canPause()) {
                    mediaPlayerComponent.getMediaPlayer().setPosition(0.0f);
                    timeSlider.setValue(0.0);
                    timeLabel.setText(playerController.getStringTime(mediaPlayerComponent.getMediaPlayer()));
                    playerController.setLastTimeDisplayed(0);
                    play.setGraphic(new ImageView(new Image("./img/pause.png")));
                    if(mediaPlayerComponent.getMediaPlayer().isPlaying())
                        mediaPlayerComponent.getMediaPlayer().pause();
                }
            }
        });

        // next button creation, with its controller
        Button next = new Button();
        next.setGraphic(new ImageView(new Image("./img/next.png")));
        next.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediaPlayerComponent.getMediaPlayer().nextChapter();
            }
        });

        // time slider creation, with its controller
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMin(0.0);
        timeSlider.setMax(100.0);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mediaPlayerComponent.getMediaPlayer().setPosition((float)(timeSlider.getValue()/100.0));
                    timeLabel.setText(playerController.getStringTime(mediaPlayerComponent.getMediaPlayer()));
                    playerController.setLastTimeDisplayed(0);
                }
            }
        });

        // time label creation
        timeLabel = new Label();
        timeLabel.setPrefWidth(130);
        timeLabel.setMinWidth(50);

        // repeat button creation, with its controller
        Button repeat = new Button();
        repeat.setGraphic(new ImageView(new Image("./img/repeat.png")));
        repeat.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayerComponent.getMediaPlayer().getRepeat()) {
                    mediaPlayerComponent.getMediaPlayer().setRepeat(false);
                    repeat.setGraphic(new ImageView(new Image("./img/random.png")));
                } else {
                    mediaPlayerComponent.getMediaPlayer().setRepeat(true);
                    repeat.setGraphic(new ImageView(new Image("./img/repeat.png")));
                }
            }
        });

        // resize button creation, with its controller
        Button resize = new Button();
        resize.setGraphic(new ImageView(new Image("./img/resize.png")));
        resize.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(stage.isFullScreen())
                    stage.setFullScreen(false);
                else
                    stage.setFullScreen(true);
            }
        });

        // Add buttons, slides and labels in the view
        hBox.getChildren().add(previous);
        hBox.getChildren().add(stop);
        hBox.getChildren().add(play);
        hBox.getChildren().add(next);
        hBox.getChildren().add(timeSlider);
        hBox.getChildren().add(timeLabel);
        hBox.getChildren().add(repeat);
        hBox.getChildren().add(resize);

        return hBox;
    }
    */

    /**
     * Create the dialog box for the connection to a client
     * @return
     */
    /*
    private Dialog<Pair<String, Integer>> getConnectionDialog() {

        Dialog<Pair<String, Integer>> dialog = new Dialog<Pair<String, Integer>>();

        dialog.setTitle("Connection to the client");
        dialog.setHeaderText("Enter the IP address of the client");

        ButtonType validateButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(validateButton, ButtonType.CANCEL);

        // Create the fields to enter the IP Address with the port number
        HBox fieldset = new HBox();
        Label startLabel = new Label("Enter client's IP address : ");
        Label colonLabel = new Label(" : ");
        TextField addr1 = new TextField("127");
        addr1.setPrefWidth(50);
        addr1.setMaxWidth(50);
        TextField addr2 = new TextField("0");
        addr2.setPrefWidth(50);
        addr2.setMaxWidth(50);
        TextField addr3 = new TextField("0");
        addr3.setPrefWidth(50);
        addr3.setMaxWidth(50);
        TextField addr4 = new TextField("1");
        addr4.setPrefWidth(50);
        addr4.setMaxWidth(50);
        TextField port = new TextField("12345");
        port.setPrefWidth(75);
        port.setMaxWidth(75);

        // Bind changes on textfields with control function
        Node button = dialog.getDialogPane().lookupButton(validateButton);
        button.setDisable(false);

        UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String strValue = change.getControlNewText();
                boolean hide = false;
                if(strValue.matches("[0-9]+")) {
                    int value = Integer.parseInt(strValue);
                    if(strValue.length() > 1 && value == 0) {
                        return null;
                    }
                    if(value >= 0 && value <= 255) {
                        return change;
                    }
                }

                return null;
            }
        };

        addr1.setTextFormatter(new TextFormatter<>(filter));
        addr2.setTextFormatter(new TextFormatter<>(filter));
        addr3.setTextFormatter(new TextFormatter<>(filter));
        addr4.setTextFormatter(new TextFormatter<>(filter));
        port.setTextFormatter(new TextFormatter<>(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
                String strValue = change.getControlNewText();
                boolean hide = false;
                if(strValue.matches("[0-9]+")) {
                    int value = Integer.parseInt(strValue);
                    if(strValue.length() > 1 && value == 0) {
                        return null;
                    }
                    if(value >= 0 && value <= 65535) {
                        return change;
                    }
                }

                return null;
            }
        }));

        fieldset.getChildren().addAll(startLabel, addr1, new Label(" . "), addr2, new Label(" . "), addr3, new Label(" . "), addr4, colonLabel, port);
        dialog.getDialogPane().setContent(fieldset);

        // Return the values of the fields on submitButton event
        dialog.setResultConverter(new Callback<ButtonType, Pair<String, Integer>>() {
            @Override
            public Pair<String, Integer> call(ButtonType param) {
                // If the user clicked on the submit button
                if(param == validateButton) {
                    String address = addr1.getText()+"."+addr2.getText()+"."+addr3.getText()+"."+addr4.getText();
                    int portNb = -1;
                    try {
                        portNb = Integer.parseInt(port.getText());
                    } catch (NumberFormatException e) {

                    }

                    return new Pair<String, Integer>(address, portNb);
                }

                return null;
            }
        });

        return dialog;
    }*/

    private Socket getClientConnection(String addr, int port) {

        Socket soc = null;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection information");
        alert.setHeaderText(null);
        alert.setContentText("The connection to the client has been successfully done !");

        try {
            if(port < 0) {
                alert.setContentText("Invalid port number !");
            } else {
                soc = new Socket(addr, port);
            }
        } catch (ConnectException e) {
            alert.setContentText("The connection to the client has been failed ! Make sure the client is already started !");
        } catch (IOException e) {
            alert.setContentText("An error occurred during making the connection to the client ! Please try later !");
        } finally {
            alert.showAndWait();
            return soc;
        }
    }

    /**
     * Start the application.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Get the player view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/views/player.fxml"));
        AnchorPane root = (AnchorPane) loader.load();

        // Initialisation of the components
        resizablePlayer = new ResizablePlayer(root);
        mediaPlayerComponent = resizablePlayer.getMediaPlayerComponent();
        //Pane playerHolder = resizablePlayer.getPlayerHolder();
        //AnchorPane root = new AnchorPane();
        //VBox vBox = new VBox();

        // Get the control bar and set the controller to the player
        //HBox hBox = getControlBar(primaryStage);
        playerController = new PlayerController(mediaPlayerComponent.getMediaPlayer(), primaryStage, root);
        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(playerController);

        // Set the player in the BorderPane
        //BorderPane bp = new BorderPane(playerHolder);
        //bp.setStyle("-fx-background-color: black");

        // First, add the BorderPane containing the player in the vBox
        //vBox.getChildren().add(bp);
        //VBox.setVgrow(bp, Priority.ALWAYS);

        // Then, add the control bar in the vBox
        //vBox.getChildren().add(hBox);

        // Add the vBox in the AnchorPane
        //root.getChildren().add(vBox);
        //AnchorPane.setTopAnchor(vBox, 0.0);
        //AnchorPane.setBottomAnchor(vBox, 0.0);
        //AnchorPane.setLeftAnchor(vBox, 0.0);
        //AnchorPane.setRightAnchor(vBox, 0.0);

        // Set the menuBar
        Menu menu = new Menu("File");
        Menu menu2 = new Menu("Client");
        MenuItem item1 = new MenuItem("Add file");
        MenuItem item2 = new MenuItem("Connect to");
        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(status.equals(CONNECTION_STATUS.CONNECTED)) {
                    try {
                        if(!socket.isClosed()) {
                            sendData.println(REQUEST_CLIENT.DECONNECTION);
                            sendData.flush();
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    item2.setText("Connect to");
                    status = CONNECTION_STATUS.DISCONNECTED;
                } else {
                    ConnectionDialog connectionDialog = new ConnectionDialog();
                    System.out.println(connectionDialog);
                    System.out.println(connectionDialog.getDialog());
                    Optional<Pair<String, Integer>> result = connectionDialog.getDialog().showAndWait();
                    if (result.isPresent()) {
                        String addr = result.get().getKey();
                        int port = result.get().getValue();

                        System.out.println("Adresse : " + addr);
                        System.out.println("Port : " + port);

                        socket = getClientConnection(addr, port);

                        if (socket != null) {
                            try {
                                sendData = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            item2.setText("Disconnect from");
                            status = CONNECTION_STATUS.CONNECTED;

                            streamMedia = new StreamMedia(socket.getInetAddress().getHostAddress(), socket.getLocalPort());
                            streamMedia.getPlayList().addMedia(PATH_TO_VIDEO);
                            /*try {
                                // Do things here with the socket


                            } catch (IOException e) {
                                System.out.println("An error occurred while trying closing the socket !");
                                e.printStackTrace();
                            }*/
                        }
                    } else {
                        System.out.println("Canceled");
                    }
                }
            }
        });

        MenuItem item3 = new MenuItem("Play");
        item3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && status.equals(CONNECTION_STATUS.CONNECTED)) {
                    streamMedia.getMediaListPlayer().play();
                    // Wait few milliseconds to make sure the MediaListPlayer is ready for the stream
                    // before client starts receiving data
                    try {
                        Thread.currentThread().sleep(100);
                        System.out.println("Play track 1 of "+streamMedia.getMediaListPlayer().getMediaList().size());
                        sendData.println(REQUEST_CLIENT.STREAMING_STARTED);
                        sendData.flush();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        MenuItem item4 = new MenuItem("Pause");
        item4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && status.equals(CONNECTION_STATUS.CONNECTED)) {
                    streamMedia.getMediaListPlayer().pause();
                }
            }
        });

        MenuItem item5 = new MenuItem("Stop");
        item5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(streamMedia != null && status.equals(CONNECTION_STATUS.CONNECTED)) {
                    streamMedia.getMediaListPlayer().stop();
                    sendData.println(REQUEST_CLIENT.STREAMING_STOPPED);
                    sendData.flush();
                }
            }
        });

        menu.getItems().addAll(item1);
        menu2.getItems().addAll(item2, item3, item4, item5);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, menu2);
        menuBar.setUseSystemMenuBar(true);

        // Set the menuBar to the AnchorPane
        root.getChildren().add(menuBar);

        // Set the AnchorPane to the scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Start playing the first media
        //String mrl = new RtspMrl().host("127.0.0.1").port(12345).path("/demo").value();
        mediaPlayerComponent.getMediaPlayer().prepareMedia(PATH_TO_VIDEO);
        //mediaPlayerComponent.getMediaPlayer().start();
        //duration = new Duration(mediaPlayerComponent.getMediaPlayer().getPosition());

        // Control the close button of the window
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                mediaPlayerComponent.release(true);
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.show();
    }

    private enum CONNECTION_STATUS {
        CONNECTED,
        DISCONNECTED
    }

    private enum REQUEST_CLIENT {
        STREAMING_STARTED,
        STREAMING_STOPPED,
        DECONNECTION
    }

    /**
     * Main function of the program.
     * @param args
     */
    public static void main(String[] args) {
        new NativeDiscovery().discover();
        Application.launch(MyApp.class);
    }
}
