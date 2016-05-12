package sample.controller;

import javafx.stage.Stage;
import javafx.util.Pair;
import sample.Main;
import sample.model.Playlist;
import sample.model.Point;
import sample.model.ResizablePlayer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private final String PATH_TO_MEDIA = "/Users/thomasfouan/Desktop/video.avi";//"C:\\Users\\Vincent\\Desktop\\video.mkv";

    public MainFrameController(String path, Stage primaryStage, Playlist list) {
        try {
            this.rootPane = (AnchorPane) loadRoot(path);
            this.rootPane.getChildren().stream().filter(node -> Objects.equals(node.getId(), "grid")).forEach(node -> {
                this.grid = (GridPane) node;
            });

            HashMap<String, Point> componentToLoad = readComponent();

            for(Map.Entry<String, Point> m : componentToLoad.entrySet()){
                AnchorPane pane = loadComponent(m.getKey());
                this.grid.add(pane, m.getValue().getX(), m.getValue().getY());
            }

            this.suggestions = loadComponent("/sample/view/suggestions.fxml");
            this.mediacase = loadComponent("/sample/view/mediacase.fxml");
            this.plugin = loadComponent("/sample/view/plugin.fxml");
            this.player = loadComponent("/sample/view/player.fxml");
            this.playlist = loadComponent("/sample/view/playlist.fxml");
            
            Pair<AnchorPane, MusicController> pairPlaylist = loadComponentAndController("/sample/view/playlist.fxml");
            this.playlist = pairPlaylist.getKey();
            MusicController musicController = pairPlaylist.getValue();
            musicController.setPlaylist(list);

            ResizablePlayer resizablePlayer = new ResizablePlayer(primaryStage, player);
            mediaPlayerComponent = resizablePlayer.getMediaPlayerComponent();

            resizablePlayer.getPlaylist().addMedia(PATH_TO_MEDIA);
            resizablePlayer.getPlaylist().addMedia("/Users/thomasfouan/Desktop/music.mp3");
            resizablePlayer.getMediaListPlayer().play();

            this.grid.setGridLinesVisible(true);

            setRowContraints();
            setColumnConstraints();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Point> readComponent(){
        HashMap<String, Point> list = new HashMap<>();
        String csvFile = "./res/interface.csv";
        BufferedReader br = null;
        String line = "";
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                String array[] = line.split(";");

                list.put(array[0], new Point(Integer.parseInt(array[1]), Integer.parseInt(array[2])));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
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
        loader.setLocation(getClass().getResource(path));
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
        loader.setLocation(getClass().getResource(path));
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
        loader.setLocation(getClass().getResource(path));
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
