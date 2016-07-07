package sample.controller;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

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
public class PluginController {

    private String jarDirname = "plugin";

    private final String packageName = "plugin";

    private List<AnchorPane> listPlugin;

    //private List<ControllerInterface> listControllerPlugin;

    /**
     * Constructor.
     * @param jarDirname
     */
    public PluginController(String jarDirname) {
        this.jarDirname = jarDirname;
        this.listPlugin = new ArrayList<AnchorPane>();
        //this.listControllerPlugin = new ArrayList<ControllerInterface>();
    }

    /**
     * Get path to the directory containing the plugins.
     * @return jarDirname
     */
    public String getJarDirname() {
        return jarDirname;
    }

    /**
     * Set path of the directory containing the plugins.
     * @param jarDirname
     */
    public void setJarDirname(String jarDirname) {
        this.jarDirname = jarDirname;
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
    public void loadJarFiles() {

        File jarDir = new File(jarDirname);
        // Check if the path containing the plugins exists and represents a directory.
        if(jarDir.exists() && jarDir.isDirectory()) {
            String[] dirContent = jarDir.list();
            File file = null;
            URL urlList[];
            ClassLoader loader;

            try {
                for(String filepath : dirContent) {
                    file = new File(jarDir.getAbsolutePath()+"/"+filepath);
                    // Check if the current file in jarDir is a file with the ".jar" extension
                    if (file.isDirectory() || !file.getPath().endsWith(".jar")) {
                        continue;
                    }

                    urlList = new URL[]{file.toURI().toURL()};
                    loader = new URLClassLoader(urlList);

                    // Get the path of the main fxml to load
                    String pathToFxml = packageName+"/mainPluginView.fxml";
                    URL urlToFxml = loader.getResource(pathToFxml);
                    if (urlToFxml != null) {
                        // If the loader founds the file, load the component attached to the file.
                        loadComponent(urlToFxml);
                    } else {
                        System.out.println("No file '"+pathToFxml+"' has been found in jar '" + file.getName() + "'");
                    }
                }
            } catch (MalformedURLException e) {
                System.out.println("URL is malformed for the file '" + file.getPath() + "'");
                e.printStackTrace();
            }
        } else {
            System.out.println("The path to directory containing all plugins hasn't been found...");
        }
    }

    /**
     * Load the main component of a fxml file with its URL.
     * @param urlToFxml
     */
    private void loadComponent(@NotNull URL urlToFxml) {

        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(urlToFxml);
            listPlugin.add(loader.load());
            //listControllerPlugin.add(loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
