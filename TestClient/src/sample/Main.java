package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.controller.MainFrameController;
import sample.model.ResizablePlayer;
import sample.model.ThreadConnection;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.IOException;

/**
 * Created by thomasfouan on 09/03/2016.
 */
public class Main extends Application {
    /*
    private ResizablePlayer resizablePlayer;

    private DirectMediaPlayerComponent mediaPlayerComponent;*/

    private MainFrameController mainFrameController;

    @Override
    public void start(Stage primaryStage) {
        /*
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
        */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/mainFrame.fxml"));
        AnchorPane rootPane;
        Scene scene;

        // Start the Thread waiting for connections
        try {
            rootPane = loader.load();
            mainFrameController = loader.getController();

            scene = new Scene(rootPane);
            primaryStage.setScene(scene);

            // Control the close button of the window
            primaryStage.setOnCloseRequest((event) -> {
                if(mainFrameController.getThreadConnections().isAlive()) {
                    mainFrameController.getThreadConnections().interrupt();
                }

                try {
                    mainFrameController.getThreadConnections().join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mainFrameController.getResizablePlayer().release();

                Platform.exit();
                System.exit(0);
            });

            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du démarrage de l'application. Attendez sa fermeture.");
            e.printStackTrace();
            if (mainFrameController.getResizablePlayer() != null) {
                mainFrameController.getResizablePlayer().release();
            }
        }
    }

    public static void main(final String[] args) {

        new NativeDiscovery().discover();
        Application.launch(Main.class);
    }
}
