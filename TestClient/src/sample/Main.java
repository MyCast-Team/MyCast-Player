package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.controller.MainFrameController;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.IOException;

/**
 * Created by thomasfouan on 09/03/2016.
 */
public class Main extends Application {

    private MainFrameController mainFrameController;

    @Override
    public void start(Stage primaryStage) {
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
