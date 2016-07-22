package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.constant.Constant;
import sample.annotation.DocumentationAnnotation;
import sample.controller.MainFrameController;
import sample.controller.SuggestionController;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.io.IOException;

@DocumentationAnnotation(author = "Vincent Rossignol, Thomas Fouan and Pierre Lochouarn", date = "01/03/2016", description = "MyCast is a media players with many functionality like suggestions, plugins, mediacase, playlist and even more !")
public class Main extends Application {

    private Stage primaryStage;
    private static Scene scene;
    private static MainFrameController mainFrameController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MyCast");
        this.primaryStage.setMinWidth(800);
        this.primaryStage.setMinHeight(600);
        this.primaryStage.getIcons().add(new Image(getClass().getResource("view/icons/icon.png").toString()));
        checkResourceFolder();
        initRootLayout();
    }

    private void checkResourceFolder() {
        String[] paths = new String[]{Constant.PATH_TO_RESOURCES, Constant.PATH_TO_PLUGIN};
        File f;
        boolean result;

        for(String path : paths) {
            f = new File(path);
            if (!f.exists()) {
                result = f.mkdir();
                if (!result) {
                    System.out.println("No permission for creating resource directory necessary to run the application.");
                    exitApp();
                }
            }
        }
    }

    /**
     * Prepare the application to stop. Release resources and save useful info
     * @throws Exception
     */
    public static void exitApp() {
        try {
            if(mainFrameController != null) {
                // Save the current interface in the interface.csv file
                MainFrameController.saveInterface();

                if(mainFrameController.getMediacaseController() != null){
                    mainFrameController.getMediacaseController().writeMediacase();
                }
            }
            SuggestionController.sendData();
        } catch (Exception e) {
            System.out.println("An error occurred when the application tried to exit. Send the following report to the dev team.");
            e.printStackTrace();
        } finally {
            releaseMainFrameController();
            Platform.exit();
            System.exit(0);
        }
    }

    private static void releaseMainFrameController() {
        if(mainFrameController != null) {
            if (mainFrameController.getPlayerController() != null) {
                mainFrameController.getPlayerController().getResizablePlayer().release();
            }
            if (mainFrameController.getIncludedMenuBarController() != null) {
                mainFrameController.getIncludedMenuBarController().getStreamMedia().release();
            }
            mainFrameController = null;
        }
    }

    public static void loadMainFrameController() {
        VBox rootLayout = null;
        FXMLLoader loader = new FXMLLoader();

        releaseMainFrameController();

        try {
            // Load root layout from fxml file
            loader.setLocation(Main.class.getResource(Constant.PATH_TO_MAIN_VIEW));
            rootLayout = loader.load();
            mainFrameController = loader.getController();
            if(scene == null) {
                scene = new Scene(rootLayout);
            } else {
                scene.setRoot(rootLayout);
            }
        } catch (IOException e) {
            System.out.println("An error occurred when the application try to start. Wait for it to close.");
            e.printStackTrace();
            exitApp();
        }
    }

    /**
     * Initializes the root layout, the main frame skeleton.
     */
    private void initRootLayout() {
        scene = null;
        loadMainFrameController();

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.D) mainFrameController.disableDragAndDrop();
            if (event.getCode() == KeyCode.E) mainFrameController.enableDragAndDrop();
        });

        this.primaryStage.setOnCloseRequest(event -> {
            exitApp();
        });

        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public static void main(String[] args) {
        new NativeDiscovery().discover();
        launch(args);
    }
}