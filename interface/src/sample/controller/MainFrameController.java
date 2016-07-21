package sample.controller;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.model.PluginManager;
import sample.model.Point;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Class to manage our main frame of the application
 */
@DocumentationAnnotation(author = "Vincent Rossignol et Thomas Fouan", date = "01/02/2016", description = "This is the main class that manage our application. We load the different components and plugins.")
public class MainFrameController extends AnchorPane {

    @FXML
    private VBox rootPane;

    @FXML
    private AnchorPane rootContent;

    @FXML
    private GridPane grid;

    @FXML
    private MenuBarController includedMenuBarController;

    @FXML
    private StatusBarController includedStatusBarController;

    private HashMap<String, Point> availableComponents;

    private PluginManager pluginManager;

    private PlayerController playerController;
    private PlaylistController playlistController;

    public MainFrameController() {
    }

    @FXML
    public void initialize() {
        AnchorPane pane;

        pluginManager = new PluginManager();
        playerController = null;
        playlistController = null;
        availableComponents = new HashMap<>();

        includedMenuBarController.setAvailableComponents(availableComponents);

        for (String str : Constant.STATIC_INTERFACES) {
            availableComponents.put(str, new Point(-1, -1));
        }
        pluginManager.loadJarFiles().forEach((str) -> availableComponents.put(str, new Point(-1, -1)));

        HashMap<String, Point> componentToLoad = readComponent();

        for(Entry<String, Point> m : componentToLoad.entrySet()) {
            Point point = availableComponents.get(m.getKey());
            if(point != null) {
                if (m.getValue().getX() >= 0 && m.getValue().getY() >= 0) {
                    if((pane = loadComponent(m.getKey())) != null) {
                        grid.add(pane, m.getValue().getX(), m.getValue().getY());
                        point.setX(m.getValue().getX());
                        point.setY(m.getValue().getY());
                    }
                }
            }
        }

        enableDragAndDrop();

        bindPlaylistToPlayer();
        bindStatusBarToControllers();

        setRowContraints();
        setColumnConstraints();
    }

    public MenuBarController getIncludedMenuBarController() {
        return includedMenuBarController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    private static HashMap<String, Point> readComponent(){
        HashMap<String, Point> list = new HashMap<>();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(Constant.PATH_TO_INTERFACE_CONF));

            while ((line = br.readLine()) != null) {
                String array[] = line.split(";");
                list.put(array[0], new Point(Integer.parseInt(array[1]), Integer.parseInt(array[2])));
            }
        } catch (IOException e) {
            System.out.println("Interface configuration not found... Empty interface will load.");
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

    private AnchorPane loadComponent(String filename) {
        AnchorPane pane;
        FXMLLoader loader = new FXMLLoader();
        File file;

        try {
            if(filename.endsWith(".jar")) {
                file = new File(Constant.PATH_TO_PLUGIN+"/"+filename);
                pane = (AnchorPane) PluginManager.loadPlugin(file);
            } else {
                loader.setLocation(getClass().getResource(filename));
                pane = loader.load();

                if (pane.getId().equals("player")) {
                    playerController = loader.getController();
                } else if (pane.getId().equals("playlist")) {
                    playlistController = loader.getController();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            pane = null;
        }

        return pane;
    }

    private void initDragAndDrop(AnchorPane pane){
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

    public void enableDragAndDrop() {
        grid.getChildren().forEach((node) -> initDragAndDrop((AnchorPane) node));
    }

    public void disableDragAndDrop() {
        grid.getChildren().forEach((node) -> disableDragAndDropPane((AnchorPane) node));
    }

    private void disableDragAndDropPane(AnchorPane pane){
        pane.setOnDragDetected(MouseEvent::consume);
        pane.setOnDragOver(DragEvent::consume);
        pane.setOnDragDropped(event -> {

        });
    }

    private void swap(String sourceStr, AnchorPane target) {
        String[] sourcePosition = sourceStr.split(";");
        Point sourcePoint = new Point(Integer.parseInt(sourcePosition[1]), Integer.parseInt(sourcePosition[0]));
        Point targetPoint = new Point(GridPane.getColumnIndex(target), GridPane.getRowIndex(target));
        String sourcePath = getKeyByValue(sourcePoint);
        String targetPath = getKeyByValue(targetPoint);
        AnchorPane source = (AnchorPane) getNodeByRowColumnIndex(sourcePoint.getY(), sourcePoint.getX());

        grid.getChildren().remove(source);
        grid.getChildren().remove(target);
        grid.add(source, targetPoint.getX(), targetPoint.getY());
        grid.add(target, sourcePoint.getX(), sourcePoint.getY());

        // Update availableComponents by theses new values
        if(sourcePath != null && targetPath != null) {
            availableComponents.replace(sourcePath, sourcePoint, targetPoint);
            availableComponents.replace(targetPath, targetPoint, sourcePoint);
        }
    }

    private String getKeyByValue(Point point) {
        for(Entry<String, Point> entry : availableComponents.entrySet()) {
            if(entry.getValue().equals(point)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Node getNodeByRowColumnIndex(int row, int column) {
        for(Node node : grid.getChildren()) {
            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }

    /**
     * Save the actual interface in the "interface.csv" file.
     */
    public void saveInterface() {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(Constant.PATH_TO_INTERFACE_CONF, false));

            for(Entry<String, Point> entry : availableComponents.entrySet()) {
                bw.write(entry.getKey()+";"+entry.getValue().getX()+";"+entry.getValue().getY());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void bindPlaylistToPlayer() {
        if(playlistController != null) {
            if (playerController != null) {
                playerController.getResizablePlayer().setPlaylist(playlistController.getPlaylist());
                playlistController.setMediaListPlayer(playerController.getResizablePlayer().getMediaListPlayer());
            }
            if(includedMenuBarController != null) {
                includedMenuBarController.getStreamMedia().setInterfacePlaylist(playlistController.getPlaylist());
                this.playlistController.setStreamingPlayer(includedMenuBarController.getStreamMedia().getMediaListPlayer());
            }
        }
    }

    private void bindStatusBarToControllers() {
        if(includedStatusBarController != null) {
            if(playerController != null) {
                playerController.setStatusLabel(includedStatusBarController.getCenterContent());
            }
            if(includedMenuBarController != null) {
                includedMenuBarController.setStatusLabel(includedStatusBarController.getRightContent());
            }
        }
    }
}
