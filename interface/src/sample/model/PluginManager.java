package sample.model;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasfouan on 07/06/16.
 *
 * Class of control of the loading of the plugins
 */
@DocumentationAnnotation(author = "Thomas Fouan", date = "07/06/2016", description = "This class manages our list of plugins at the beginning of the session.")
public class PluginManager {

    private List<String> listPlugin;

    //private List<ControllerInterface> listControllerPlugin;

    /**
     * Constructor.
     */
    public PluginManager() {
        this.listPlugin = new ArrayList<String>();
        //this.listControllerPlugin = new ArrayList<ControllerInterface>();
    }

    /**
     * Get the list of mainView of each plugin (list of Component)
     * @return listPlugin.
     */
    public List getListPlugin() {
        return listPlugin;
    }

    /**
     * Main method of the class. Get all ".jar" files in the path define in jarDirname.
     * Try to load the mainView. It must be in packageName, with "mainPluginView.fxml" as name.
     */
    public List<String> loadJarFiles() {

        File jarDir = new File(Constant.PATH_TO_PLUGIN);
        // Check if the path containing the plugins exists and represents a directory.
        if(jarDir.exists() && jarDir.isDirectory()) {
            String[] dirContent = jarDir.list();
            File file;

            for(String filepath : dirContent) {
                file = new File(jarDir.getAbsolutePath() + "/" + filepath);
                // Add the name of the current file in the list of plugin if it is a valid plugin
                if (checkPluginValidity(file, false)) {
                    listPlugin.add(file.getName());
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Warning");
            alert.setHeaderText("Path to plugin");
            alert.setContentText("The path to directory containing all plugins hasn't been found...");
            alert.showAndWait();
        }

        return listPlugin;
    }

    /**
     * Check if the classLoader of a plugin finds the main plugin view
     * @param classLoader
     * @return True if it found. Otherwise, return false.
     */
    private static boolean isMainViewPresent(ClassLoader classLoader) {
        return (classLoader.getResource(Constant.MAIN_PLUGIN_VIEW_LOCATION) != null);
    }

    /**
     * Get a ClassLoadez plugin
     * @param plugin
     * @return ClassLoader
     */
    private static ClassLoader getPluginClassLoader(File plugin) {
        URL[] urls;
        ClassLoader classLoader;

        try {
            urls = new URL[]{plugin.toURI().toURL()};
            classLoader = new URLClassLoader(urls, PluginManager.class.getClassLoader());
        } catch (MalformedURLException e) {
            classLoader = null;
            e.printStackTrace();
        }

        return classLoader;
    }

    /**
     * Load the root pane of a fxml file to a plugin, and return it.
     * @param plugin
     */
    public static Pane loadPlugin(@NotNull File plugin) {

        URL res;
        FXMLLoader loader;
        ClassLoader classLoader;
        Pane pane = null;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Plugin load");
        alert.setHeaderText("Load plugin error");

        try {
            classLoader = getPluginClassLoader(plugin);
            res = classLoader.getResource(Constant.MAIN_PLUGIN_VIEW_LOCATION);
            if (res != null) {
                loader = new FXMLLoader(res);
                loader.setClassLoader(classLoader);
                pane = loader.load();
            } else {
                alert.setContentText("The main view of the '" + plugin.getName() + "' plugin hasn't could be found.");
                alert.showAndWait();
            }

        } catch (IOException e) {
            alert.setContentText("The pane hasn't could be load. Check your fxml file or the path to its attached controller.\"");
            alert.showAndWait();
            pane = null;
            e.printStackTrace();
        }

        return pane;
    }

    /**
     * Check the validity of a new plugin added by the user for the community. See below all the constraints that must be respect.
     * A plugin must :
     *  - be a jar file (.jar extension)
     *  - contain a package name "plugin"
     *  - contain a main view name "mainPluginView.fxml" inside the "plugin" package, and with an AnchorPane as root pane
     * @param file representing the plugin
     * @return true if the plugin respects all of the constraints. Otherwise, return false
     */
    public static boolean checkPluginValidity(File file, boolean isAlertShowing) {

        if(file == null)
            return false;

        String filename = file.getName();
        boolean isValid = false;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Plugin");
        alert.setHeaderText("Plugin validity error");

        if(file.exists()) {
            if (filename.endsWith(".jar")) {
                if (isMainViewPresent(getPluginClassLoader(file))) {
                    isValid = true;
                } else {
                    alert.setContentText("The main view of the '" + file.getName() + "' plugin hasn't could be found.");
                }
            } else {
                alert.setContentText("The selected file is not a jar file (.jar extension).");
            }
        } else {
            alert.setContentText("The selected file doesn't exist.");
        }

        if(!isValid && isAlertShowing) {
            alert.showAndWait();
        }

        return isValid;
    }
}
