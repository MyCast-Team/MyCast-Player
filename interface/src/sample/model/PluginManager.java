package sample.model;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
            File file = null;
            URL urlList[];
            ClassLoader loader;

            try {
                for(String filepath : dirContent) {
                    file = new File(jarDir.getAbsolutePath() + "/" + filepath);
                    // Check if the current file in jarDir is a file with the ".jar" extension
                    if (file.isDirectory() || !file.getPath().endsWith(".jar")) {
                        continue;
                    }

                    urlList = new URL[]{file.toURI().toURL()};
                    loader = new URLClassLoader(urlList);

                    // Get the path of the main fxml to load
                    String pathToFxml = Constant.PACKAGE_PLUGIN_NAME + "/mainPluginView.fxml";
                    URL urlToFxml = loader.getResource(pathToFxml);
                    if (urlToFxml != null) {
                        // If the loader founds the file, load the component attached to the file.
                        //loadComponent(urlToFxml);
                        listPlugin.add(urlToFxml.toString());
                    } else {
                        System.out.println("No file '" + pathToFxml + "' has been found in jar '" + file.getName() + "'");
                    }
                }
            } catch (MalformedURLException e) {
                System.out.println("URL is malformed for the file '" + file.getPath() + "'");
                e.printStackTrace();
            }
        } else {
            System.out.println("The path to directory containing all plugins hasn't been found...");
        }

        return listPlugin;
    }

    /**
     * Load the main component of a fxml file with its URL.
     * @param urlToFxml
     */
    private void loadComponent(@NotNull URL urlToFxml) {

        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(urlToFxml);
            AnchorPane pane = loader.load();
            System.out.println(pane);
            //listPlugin.add(loader.load());
            //listControllerPlugin.add(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the validity of a new plugin added by the user for the community. See below all the constraints that must be respect.
     * A plugin must :
     *  - be a jar file (.jar extension)
     *  - contain a package name "plugin"
     *  - contain a main view name "mainPluginView.fxml" inside the "plugin" package, and with AnchorPane as root pane
     * @param file representing the plugin
     * @return true if the plugin respects all of the constraints. Otherwise, return false
     */
    public static boolean checkPluginValidity(File file) {
        URL[] urls;
        URL res;
        ClassLoader classLoader;
        FXMLLoader loader;
        Pane pane;
        boolean isValid = false;
        String filename = file.getName();

        try {
            if(filename.substring(filename.lastIndexOf(".")).equals(".jar")) {

                urls = new URL[]{file.toURI().toURL()};
                classLoader = new URLClassLoader(urls, PluginManager.class.getClassLoader());
                res = classLoader.getResource("plugin/mainPluginView.fxml");

                if (res != null) {
                    loader = new FXMLLoader(res);
                    pane = loader.load();
                    /*
                    if (pane != null) {
                        isValid = true;
                    } else {
                        System.out.println("UPLOAD PLUGIN ERROR : The pane haven't could be load. Check your fxml file or the path to your attached controller.");
                    }*/
                } else {
                    System.out.println("UPLOAD PLUGIN ERROR : The main view haven't could be found");
                }
            } else {
                System.out.println("UPLOAD PLUGIN ERROR : The selected file is not a jar file (.jar extension).");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("UPLOAD PLUGIN ERROR : The pane haven't could be load. Check your fxml file or the path to your attached controller.");
            e.printStackTrace();
        }

        return isValid;
    }
}
