package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.controller.MainFrameController;
import sample.controller.MenuBarController;
import sample.model.MP3Music;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class Main extends Application {

    private Stage primaryStage;
    private MainFrameController mainFrameController;
    private MenuBarController menuBarController;
    private ObservableList<MP3Music> musicData = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MyCast");

        musicData.add(new MP3Music("Pigs out of wind", "Pink Floyd", "3:25"));
        musicData.add(new MP3Music("Space Oddity", "David Bowie", "4:45"));

        initRootLayout();
    }

    public ObservableList<MP3Music> getMusicData() {
        return musicData;
    }

    /**
     * Initializes the root layout, the main frame skeleton.
     */
    public void initRootLayout() {
        // Load root layout from fxml file

        mainFrameController = new MainFrameController("view/mainFrame.fxml", primaryStage);
        menuBarController = new MenuBarController(mainFrameController.getRootPane());
        AnchorPane rootLayout = mainFrameController.getRootPane();

        Scene scene = new Scene(rootLayout);

        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.D) mainFrameController.disableDragAndDrop();
            if(event.getCode() == KeyCode.E) mainFrameController.enableDragAndDrop();
        });

        primaryStage.setOnCloseRequest(event -> {
            mainFrameController.getMediaPlayer().release(true);
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