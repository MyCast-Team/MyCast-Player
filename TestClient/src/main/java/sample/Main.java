package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.IOException;

/**
 * Created by thomasfouan on 09/03/2016.
 */
public class Main extends Application {

    static final String PATH_TO_MAIN_VIEW = "/fxml/mainFrame.fxml";
    private static final String PATH_TO_ICON = "/icons/icon.png";

    private MainFrameController mainFrameController;

    @Override
    public void stop() {
        try {
            super.stop();
            if(mainFrameController != null) {
                mainFrameController.release();
            }
        } catch (Exception e) {
            System.out.println("An error occurred when the application tried to exit. Send the following report to the dev team.");
            e.printStackTrace();
        } finally {
            Platform.exit();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_TO_MAIN_VIEW));
        AnchorPane rootPane;
        Scene scene;

        // Start the Thread waiting for connections
        try {
            rootPane = loader.load();
            mainFrameController = loader.getController();

            scene = new Scene(rootPane);
            primaryStage.setScene(scene);

            // Control the close button of the window
            primaryStage.setOnCloseRequest((event) -> stop());

            primaryStage.setTitle("MyCast");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.getIcons().add(new Image(getClass().getResource(PATH_TO_ICON).toString()));
            primaryStage.setFullScreen(false);
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
