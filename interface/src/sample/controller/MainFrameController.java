package sample.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Class to manage our main frame of the application
 */
@DocumentationAnnotation(author = "Vincent Rossignol et Thomas Fouan", date = "01/02/2016", description = "This is the main class that manage our application. We load the different components and plugins.")
public class MainFrameController extends AnchorPane {

    private VBox rootPane;

    private AnchorPane rootContentPane;

    private GridPane grid;

    private HashMap<String, Point> availableComponents;

    private ArrayList<AnchorPane> components;

    private PluginManager pluginManager;

    private PlayerController playerController;
    private PlaylistController playlistController;
    private MenuBarController menuBarController;
    private StatusBarController statusBarController;

    public MainFrameController(String path, Stage primaryStage) {

        this.pluginManager = new PluginManager();
        this.playerController = null;
        this.playlistController = null;
        this.availableComponents = new HashMap<>();
        this.components = new ArrayList<>();

        try {
            AnchorPane pane;

            this.rootPane = (VBox) loadRoot(path);
            rootPane.getChildren().add(0, loadMenuBar());
            rootPane.getChildren().add(2, loadStatusBar());

            this.rootPane.getChildren().stream().filter(node -> Objects.equals(node.getId(), "rootContent")).forEach(node -> {
                this.rootContentPane = (AnchorPane) node;
            });

            this.rootContentPane.getChildren().stream().filter(node -> Objects.equals(node.getId(), "grid")).forEach(node -> {
                this.grid = (GridPane) node;
            });

            for (String str : Constant.staticInterfaces) {
                availableComponents.put(str, new Point(-1, -1));
            }
            pluginManager.loadJarFiles().forEach((str) -> availableComponents.put(str, new Point(-1, -1)));

            HashMap<String, Point> componentToLoad = readComponent();

            for(Entry<String, Point> m : componentToLoad.entrySet()) {
                Point point = availableComponents.get(m.getKey());
                if(point != null) {
                    if (m.getValue().getX() >= 0 && m.getValue().getY() >= 0) {
                        if((pane = loadComponent(m.getKey())) != null) {
                            this.grid.add(pane, m.getValue().getX(), m.getValue().getY());
                            this.components.add(pane);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VBox getRootPane(){
        return rootPane;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public MenuBarController getMenuBarController() {
        return menuBarController;
    }

    private static HashMap<String, Point> readComponent(){
        HashMap<String, Point> list = new HashMap<>();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(Constant.pathToInterfaceConf));

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

    private AnchorPane loadComponent(String path){
        AnchorPane pane;
        FXMLLoader loader = new FXMLLoader();

        try {
            if(path.startsWith("jar:file:")) {
                loader.setLocation(new URL(path));
                pane = loader.load();
            } else {
                loader.setLocation(getClass().getResource(path));
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

    private Pane loadRoot(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        return loader.load();
    }

    private MenuBar loadMenuBar() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/view/menubar.fxml"));
        MenuBar menuBar = loader.load();
        this.menuBarController = loader.getController();
        menuBarController.setAvailableComponents(availableComponents);
        return menuBar;
    }

    private HBox loadStatusBar() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/view/statusBar.fxml"));
        HBox pane = loader.load();
        this.statusBarController = loader.getController();
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
        components.forEach(this::initDragAndDrop);
    }

    public void disableDragAndDrop() {
        components.forEach(this::disableDragAndDropPane);
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
        ObservableList<Node> childrens = grid.getChildren();
        for(Node node : childrens) {
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
            bw = new BufferedWriter(new FileWriter(Constant.pathToInterfaceConf, false));

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
        if(this.playlistController != null) {
            if (this.playerController != null) {
                this.playerController.getResizablePlayer().setPlaylist(this.playlistController.getPlaylist());
                this.playlistController.setMediaListPlayer(this.playerController.getResizablePlayer().getMediaListPlayer());
            }
            if(this.menuBarController != null) {
                this.menuBarController.getStreamMedia().setInterfacePlaylist(playlistController.getPlaylist());
                this.playlistController.setStreamingPlayer(menuBarController.getStreamMedia().getMediaListPlayer());
            }
        }
    }

    private void bindStatusBarToControllers() {
        if(statusBarController != null) {
            if(playerController != null) {
                playerController.setStatusLabel(statusBarController.getCenterContent());
            }
            if(menuBarController != null) {
                menuBarController.setStatusLabel(statusBarController.getRightContent());
            }
        }
    }
}
