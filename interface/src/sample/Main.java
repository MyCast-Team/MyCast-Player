package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.annotation.DocumentationAnnotation;
import sample.controller.MainFrameController;
import sample.controller.MenuBarController;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@DocumentationAnnotation(author = "Vincent Rossignol, Thomas Fouan and Pierre Lochouarn", date = "01/03/2016", description = "MyShare is a media players with many functionality like suggestions, plugins, mediacase, playlist and even more !")
public class Main extends Application {

    private Stage primaryStage;
    private MainFrameController mainFrameController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MyCast");
        this.primaryStage.getIcons().add(new Image(getClass().getResource("view/icons/icon.png").toString()));
        checkResourceFolder();
        initRootLayout();
    }

    public void checkResourceFolder(){
        File f = new File("./res");
        if (!f.exists()) {
            boolean result = f.mkdir();
            if(!result)
                System.out.println("No permission for creating directory");
        }
    }
    /**
     * Initializes the root layout, the main frame skeleton.
     */
    public void initRootLayout() {
        // Load root layout from fxml file
        mainFrameController = new MainFrameController("/sample/view/mainFrame.fxml", this.primaryStage);
        VBox rootLayout = mainFrameController.getRootPane();

        Scene scene = new Scene(rootLayout);

        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.D) mainFrameController.disableDragAndDrop();
            if(event.getCode() == KeyCode.E) mainFrameController.enableDragAndDrop();
        });

        primaryStage.setOnCloseRequest(event -> {
            if(mainFrameController.getPlayerController() != null) {
                mainFrameController.getPlayerController().getResizablePlayer().release();
            }
            if(mainFrameController.getMenuBarController() != null) {
                mainFrameController.getMenuBarController().getStreamMedia().release();
            }
            Platform.exit();
            System.exit(0);
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Returns the main stage.
     * @return primaryStage which is the only stage at the time
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        new NativeDiscovery().discover();
        launch(args);
    }
}