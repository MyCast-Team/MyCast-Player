package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.controller.MainFrameController;
import sample.controller.MenuBarController;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class Main extends Application {

    private Stage primaryStage;
    private MainFrameController mainFrameController;
    private MenuBarController menuBarController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MyCast");

        initRootLayout();
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