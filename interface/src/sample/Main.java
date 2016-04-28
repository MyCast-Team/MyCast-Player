package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.controller.MainFrameController;
import sample.controller.MenuBarController;
import sample.controller.MusicController;
import sample.model.Media;
import sample.model.Music;
import sample.model.Playlist;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.util.ArrayList;

public class Main extends Application {

    private Stage primaryStage;
    private MainFrameController mainFrameController;
    private MenuBarController menuBarController;
    private Playlist playlist;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MyCast");

        playlist = new Playlist();
        //playlist.readPlaylist();
        initRootLayout();
    }

    /**
     * Initializes the root layout, the main frame skeleton.
     */
    public void initRootLayout() {
        // Load root layout from fxml file
        mainFrameController = new MainFrameController("view/mainFrame.fxml", this.primaryStage, this.playlist);
        menuBarController = new MenuBarController(mainFrameController.getRootPane());
        AnchorPane rootLayout = mainFrameController.getRootPane();

        Scene scene = new Scene(rootLayout);

        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.D) mainFrameController.disableDragAndDrop();
            if(event.getCode() == KeyCode.E) mainFrameController.enableDragAndDrop();
        });

        primaryStage.setOnCloseRequest(event -> {
            mainFrameController.getMediaPlayer().release(true);
            playlist.writePlaylist();
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

    public ArrayList<Music> getPlaylist() { return playlist.getPlaylist(); }

    public static void main(String[] args) {
        new NativeDiscovery().discover();
        launch(args);
    }
}