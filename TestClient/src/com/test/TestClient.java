package com.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.mrl.RtspMrl;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by thomasfouan on 09/03/2016.
 */
public class TestClient extends Application {

    private ResizablePlayer resizablePlayer;

    private DirectMediaPlayerComponent mediaPlayerComponent;

    private Thread threadConnections;

    private static final String PATH_TO_VIDEO = "/Users/thomasfouan/Desktop/video.avi";

    /*
    private static MediaPlayerFactory mFactory;

    private static HeadlessMediaPlayer mPlayer;

    private static void simpleClient() {
        mFactory = new MediaPlayerFactory();
        mPlayer = mFactory.newHeadlessMediaPlayer();

        //String mrl = "rtsp://@" + "127.0.0.1" + ":" + "12345/demo";
        //String file = "/Users/thomasfouan/Music/Daft Punk/Discovery/01 - One More Time.mp3";

        String mrl = new RtspMrl().host("127.0.0.1").port(12345).path("/demo").value();
        mPlayer.playMedia(mrl);

        Thread admin = new Thread() {

            @Override
            public void interrupt() {
                super.interrupt();
            }

            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                String entry;
                while(true) {
                    entry = sc.nextLine();

                    if(entry.equals("quit")) {
                        System.out.println("Quit !");
                        break;
                    } else {
                        System.out.println("Unknown command ^^");
                    }
                }

                mPlayer.stop();
                mPlayer.release();
                mFactory.release();
            }
        };

        admin.start();
        try {
            admin.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    */

    @Override
    public void start(Stage primaryStage) {

        resizablePlayer = new ResizablePlayer();
        mediaPlayerComponent = resizablePlayer.getMediaPlayerComponent();
        Pane playerHolder = resizablePlayer.getPlayerHolder();
        AnchorPane root = new AnchorPane();
        VBox vBox = new VBox();

        // Set the player in the BorderPane
        BorderPane bp = new BorderPane(playerHolder);
        bp.setStyle("-fx-background-color: black");

        // First, add the BorderPane containing the player in the vBox
        vBox.getChildren().add(bp);
        VBox.setVgrow(bp, Priority.ALWAYS);

        // Add the vBox in the AnchorPane
        root.getChildren().add(vBox);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);

        // Set the AnchorPane to the scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Start playing the first media
        //mediaPlayerComponent.getMediaPlayer().prepareMedia(PATH_TO_VIDEO);
        //mediaPlayerComponent.getMediaPlayer().start();

        // Start the Thread waiting for connections
        try {
            threadConnections = new ThreadConnection(mediaPlayerComponent.getMediaPlayer());
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadConnections.start();

        // Control the close button of the window
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Server will stop...");

                if(threadConnections.isAlive()) {
                    System.out.println("Interruption of the ThreadConnection !");
                    threadConnections.interrupt();
                }

                System.out.println("Waiting for join...");
                try {
                    threadConnections.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mediaPlayerComponent.release(true);

                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.show();
    }

    public static void main(final String[] args) {

        new NativeDiscovery().discover();
        //simpleClient();
        //computeConnections();
        Application.launch(TestClient.class);
        System.out.println("End of the main thread !");
    }
}
