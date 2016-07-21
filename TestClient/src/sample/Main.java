package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.constant.Constant;
import sample.controller.MainFrameController;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.IOException;

/**
 * Created by thomasfouan on 09/03/2016.
 */
public class Main extends Application {

    private MainFrameController mainFrameController;

    @Override
    public void stop() {
        try {
            super.stop();
            if(mainFrameController != null) {
                if(mainFrameController.getThreadConnections() != null) {
                    if (mainFrameController.getThreadConnections().isAlive()) {
                        mainFrameController.getThreadConnections().interrupt();
                    }
                    mainFrameController.getThreadConnections().join();
                }
                mainFrameController.getResizablePlayer().release();
            }
        } catch (Exception e) {
            System.out.println("An error occurred when the application tried to exit. Send the following report to the dev team.");
            e.printStackTrace();
        } finally {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constant.pathToMainView));
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
                stop();
            });

            primaryStage.setTitle("MyCast");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.getIcons().add(new Image(getClass().getResource("view/icons/icon.png").toString()));
            primaryStage.setFullScreen(true);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error while starting the application. Wait for it to close.");
            e.printStackTrace();
            stop();
        }
    }

    public static void main(final String[] args) {
        new NativeDiscovery().discover();
        launch(args);
    }
}
