package sample.controller;

import javafx.stage.Stage;
import javafx.util.Pair;
import sample.model.Playlist;
import sample.model.ResizablePlayer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import sample.Main;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;

import java.io.IOException;
import java.util.Objects;

/**
 * Class to manage our main frame of the application
 */
public class MainFrameController extends AnchorPane {
    private AnchorPane rootPane;
    private GridPane grid;
    public AnchorPane playlist;
    public AnchorPane suggestions;
    public AnchorPane mediacase;
    public AnchorPane plugin;
    public AnchorPane player;
    public DirectMediaPlayerComponent mediaPlayerComponent;
    private final String PATH_TO_MEDIA = "C:\\Users\\Vincent\\Desktop\\video.mkv";//"/Users/thomasfouan/Desktop/video.avi";

    public MainFrameController(String path, Stage primaryStage, Playlist list) {
        try {
            this.rootPane = (AnchorPane) loadRoot(path);
            this.rootPane.getChildren().stream().filter(node -> Objects.equals(node.getId(), "grid")).forEach(node -> {
                this.grid = (GridPane) node;
            });

            this.suggestions = loadComponent("view/suggestions.fxml");
            this.mediacase = loadComponent("view/mediacase.fxml");
            this.plugin = loadComponent("view/plugin.fxml");
            this.player = loadComponent("view/player.fxml");
            this.playlist = loadComponent("view/playlist.fxml");
            Pair<AnchorPane, MusicController> pairPlaylist = loadComponentAndController("view/playlist.fxml");
            this.playlist = pairPlaylist.getKey();
            MusicController musicController = pairPlaylist.getValue();
            musicController.setPlaylist(list);

            ResizablePlayer resizablePlayer = new ResizablePlayer(this.player);

            this.mediaPlayerComponent = resizablePlayer.getMediaPlayerComponent();

            MediaPlayer mediaPlayer = this.mediaPlayerComponent.getMediaPlayer();

            PlayerController playerController = new PlayerController((DirectMediaPlayer) mediaPlayer, primaryStage, this.player);
            mediaPlayer.addMediaPlayerEventListener(playerController);

            mediaPlayer.prepareMedia(this.PATH_TO_MEDIA);
            mediaPlayer.start();

            this.grid.add(this.player, 0, 0);
            this.grid.add(this.playlist, 0, 1);
            this.grid.add(this.mediacase, 1, 0);
            this.grid.add(this.plugin, 1, 1);

            this.grid.setGridLinesVisible(true);

            setRowContraints();
            setColumnConstraints();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRowContraints() {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(50.0);
        rowConstraints.setVgrow(Priority.ALWAYS);
        // On l'applique deux fois : une pour chaque ligne
        grid.getRowConstraints().add(rowConstraints);
        grid.getRowConstraints().add(rowConstraints);
    }

    private void setColumnConstraints() {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50.0);
        columnConstraints.setHgrow(Priority.ALWAYS);
        // On l'applique deux fois : une pour chaque colonne
        grid.getColumnConstraints().add(columnConstraints);
        grid.getColumnConstraints().add(columnConstraints);
    }

    public AnchorPane loadComponent(String path){
        AnchorPane pane = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(path));
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initDragAndDrop(pane);
        return pane;
    }

    public Pair loadComponentAndController(String path){
        AnchorPane pane = null;
        Object controller = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(path));
        try {
            pane = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initDragAndDrop(pane);
        return new Pair(pane, controller);
    }

    public Pane loadRoot(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource(path));
        return loader.load();
    }

    public void initDragAndDrop(AnchorPane pane){
        pane.setOnDragDetected(event -> {
            Dragboard db = pane.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(pane.getId());
            db.setContent(content);
            event.consume();
        });

        pane.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        pane.setOnDragDropped(event -> swap(event.getDragboard().getString(), pane));
    }

    public void enableDragAndDrop(){
        initDragAndDrop(playlist);
        initDragAndDrop(mediacase);
        initDragAndDrop(suggestions);
        initDragAndDrop(plugin);
    }

    public void disableDragAndDrop(){
        disableDragAndDropPane(playlist);
        disableDragAndDropPane(mediacase);
        disableDragAndDropPane(suggestions);
        disableDragAndDropPane(plugin);
    }

    public void disableDragAndDropPane(AnchorPane pane){
        pane.setOnDragDetected(MouseEvent::consume);

        pane.setOnDragOver(DragEvent::consume);

        pane.setOnDragDropped(event -> {
        });
    }

    public void swap(String sourceStr, AnchorPane target){
        Node source = null;
        switch(sourceStr){
            case "playlist":
                source = playlist;
                break;
            case "suggestions":
                source = suggestions;
                break;
            case "mediacase":
                source = mediacase;
                break;
            case "plugin":
                source = plugin;
                break;
        }
        int sourceRow = GridPane.getRowIndex(source);
        int sourceColumn = GridPane.getColumnIndex(source);
        int targetRow = GridPane.getRowIndex(target);
        int targetColumn = GridPane.getColumnIndex(target);
        grid.getChildren().remove(source);
        grid.getChildren().remove(target);
        grid.add(source, targetColumn, targetRow);
        grid.add(target, sourceColumn, sourceRow);
    }

    public AnchorPane getRootPane(){
        return rootPane;
    }

    public DirectMediaPlayerComponent getMediaPlayer(){
        return mediaPlayerComponent;
    }
}
