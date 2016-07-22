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

    public static HashMap<String, Point> availableComponents;

    private PluginManager pluginManager;

    private static PlayerController playerController;
    private static PlaylistController playlistController;
    private static MediacaseController mediacaseController;

    public MainFrameController() {
    }

    @FXML
    public void initialize() {
        pluginManager = new PluginManager();
        playerController = null;
        playlistController = null;
        mediacaseController = null;
        availableComponents = new HashMap<>();

        initComponentsPosition();
        loadGridPane();

        enableDragAndDrop();

        bindControllers();

        setRowContraints();
        setColumnConstraints();
    }

    public MenuBarController getIncludedMenuBarController() {
        return includedMenuBarController;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public MediacaseController getMediacaseController() {
        return mediacaseController;
    }

    /**
     * Initialize the position of available components with previous interface.csv file.
     * If the file doesn't exist, load an empty interface
     */
    private void initComponentsPosition() {
        // Init the hashmap of available interfaces by default interfaces and valid plugins. Set their position to (-1;-1)
        for (String str : Constant.STATIC_INTERFACES) {
            availableComponents.put(str, new Point(-1, -1));
        }
        pluginManager.loadJarFiles().forEach((str) -> availableComponents.put(str, new Point(-1, -1)));

        // Get the previous user interface configuration if it exists
        HashMap<String, Point> componentToLoad = readComponent();

        // Update the position of availableComponents with componentToLoad
        for(Entry<String, Point> m : componentToLoad.entrySet()) {
            Point point = availableComponents.get(m.getKey());
            if(point != null) {
                if (m.getValue().getX() >= 0 && m.getValue().getY() >= 0) {
                    point.setX(m.getValue().getX());
                    point.setY(m.getValue().getY());
                }
            }
        }
    }

    /**
     * Set each interface at its position in the GridPane of the application
     */
    private void loadGridPane() {
        AnchorPane pane;
        grid.getChildren().clear();
        for(Entry<String, Point> entry : availableComponents.entrySet()) {
            if (entry.getValue().getX() >= 0 && entry.getValue().getY() >= 0) {
                if ((pane = loadComponent(entry.getKey())) != null) {
                    pane.setStyle("-fx-border-color: #B0B0B0; -fx-border-style : solid; -fx-border-width : 1 1 0 1;");
                    grid.add(pane, entry.getValue().getX(), entry.getValue().getY());
                }
            }
        }
    }

    /**
     * Get previous interface configuration from interface.csv
     * @return HashMap<String, Point>
     */
    private HashMap<String, Point> readComponent(){
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

    /**
     * Load a component by its name. Return an AnchorPane representing the main Pane of the interface
     * @param filename
     * @return AnchorPane
     */
    private AnchorPane loadComponent(String filename) {
        AnchorPane pane;
        FXMLLoader loader = new FXMLLoader();
        File file;

        try {
            if(filename.endsWith(".jar")) {
                file = new File(Constant.PATH_TO_PLUGIN+"/"+filename);
                pane = (AnchorPane) PluginManager.loadPlugin(file);
            } else {
                loader.setLocation(MainFrameController.class.getResource(filename));
                pane = loader.load();

                if (pane.getId().equals("player")) {
                    playerController = loader.getController();
                } else if (pane.getId().equals("playlist")) {
                    playlistController = loader.getController();
                } else if (pane.getId().equals("mediacase")) {
                    mediacaseController = loader.getController();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            pane = null;
        }

        return pane;
    }

    /**
     * Set the drag'n'drop on an AnchorPane
     * @param pane
     */
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

    /**
     * Disable the drag'n'drop on an AnchorPane
     * @param pane
     */
    private void disableDragAndDropPane(AnchorPane pane){
        pane.setOnDragDetected(MouseEvent::consume);
        pane.setOnDragOver(DragEvent::consume);
        pane.setOnDragDropped(event -> {

        });
    }

    /**
     * Enable the drag'n'drop functionality of each interface in the GridPane
     */
    public void enableDragAndDrop() { grid.getChildren().forEach((node) -> initDragAndDrop((AnchorPane) node)); }

    /**
     *  Disable the drag'n'drop functionality of each interface in the GridPane
     */
    public void disableDragAndDrop() { grid.getChildren().forEach((node) -> disableDragAndDropPane((AnchorPane) node)); }

    /**
     * Swap 2 interfaces in the GridPane after a drag'n'drop
     * @param sourceStr
     * @param target
     */
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

    /**
     * Return the name of an interface by its position in availableComponents
     * @param point
     * @return Key
     */
    private String getKeyByValue(Point point) {
        for(Entry<String, Point> entry : availableComponents.entrySet()) {
            if(entry.getValue().equals(point)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Get a node in the GridPane by its coordinates
     * @param row
     * @param column
     * @return Node
     */
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
    public static void saveInterface() {
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

    /**
     * Bind Controllers to communicate in each other
     */
    private void bindControllers() {
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
