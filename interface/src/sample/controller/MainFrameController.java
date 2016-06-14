package sample.controller;

import javafx.collections.ObservableList;
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

    private ArrayList<AnchorPane> components;

    private PlayerController playerController;

    private PlaylistController playlistController;

    public MainFrameController(String path, Stage primaryStage) {

        this.playerController = null;
        this.playlistController = null;
        this.components = new ArrayList<>();

        try {
            this.rootPane = (AnchorPane) loadRoot(path);
            this.rootPane.getChildren().stream().filter(node -> Objects.equals(node.getId(), "grid")).forEach(node -> {
                this.grid = (GridPane) node;
            });

            HashMap<String, Point> componentToLoad = readComponent();

            for(Map.Entry<String, Point> m : componentToLoad.entrySet()){
                AnchorPane pane = loadComponent(m.getKey());
                this.grid.add(pane, m.getValue().getX(), m.getValue().getY());
                this.components.add(pane);
            }

            enableDragAndDrop();

            bindPlaylistToPlayer();

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

            if(pane.getId().equals("player")) {
                playerController = loader.getController();
            } else if(pane.getId().equals("playlist")) {
                playlistController = loader.getController();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
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
            content.putString(GridPane.getRowIndex(pane)+";"+GridPane.getColumnIndex(pane));
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
        components.forEach(this::initDragAndDrop);
    }

    public void disableDragAndDrop(){
        components.forEach(this::disableDragAndDropPane);
    }

    public void disableDragAndDropPane(AnchorPane pane){
        pane.setOnDragDetected(MouseEvent::consume);

        pane.setOnDragOver(DragEvent::consume);

        pane.setOnDragDropped(event -> {
        });
    }

    public void swap(String sourceStr, AnchorPane target){
        String[] sourcePosition = sourceStr.split(";");
        int sourceRow = Integer.parseInt(sourcePosition[0]);
        int sourceColumn = Integer.parseInt(sourcePosition[1]);
        int targetRow = GridPane.getRowIndex(target);
        int targetColumn = GridPane.getColumnIndex(target);
        AnchorPane source = (AnchorPane) getNodeByRowColumnIndex(sourceRow, sourceColumn);
        grid.getChildren().remove(source);
        grid.getChildren().remove(target);
        grid.add(source, targetColumn, targetRow);
        grid.add(target, sourceColumn, sourceRow);
    }

    public Node getNodeByRowColumnIndex(int row, int column) {
        ObservableList<Node> childrens = grid.getChildren();
        for(Node node : childrens) {
            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }

    private void bindPlaylistToPlayer() {
        if(this.playlistController != null && this.playerController != null) {
            this.playerController.getResizablePlayer().setPlaylist(this.playlistController.getPlaylist());
            this.playlistController.setMediaListPlayer(this.playerController.getResizablePlayer().getMediaListPlayer());
        }
    }

    public AnchorPane getRootPane(){
        return rootPane;
    }
}
