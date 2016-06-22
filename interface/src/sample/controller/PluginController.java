package sample.controller;

<<<<<<< HEAD
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sample.model.Plugin;
import sample.model.StreamMedia;
import sun.plugin2.util.PluginTrace;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Pierre on 30/05/2016.
 */
public class PluginController {
    @FXML
    private TableView<Plugin> pluginTable;
    @FXML
    private TableColumn<Plugin, String> nameColumn1;
    @FXML
    private TableColumn<Plugin, String> authorColumn1;
    @FXML
    private TableColumn<Plugin, String> dateColumn1;
    @FXML
    private Button refresh;

    private ArrayList<Plugin> pluginList;
    final String path = "./PluginsList/plugin.json";
    public PluginController(){

    }


    @FXML
    public void initialize(){
        pluginList = new ArrayList<>();
        getList();
        ObservableList<Plugin> list = FXCollections.observableArrayList(pluginList);
        pluginTable.setItems(list);
        nameColumn1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        authorColumn1.setCellValueFactory(cellData -> cellData.getValue().AuthorProperty());
        dateColumn1.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        refresh.setOnAction(getRefreshEventHandler());
    }

    public EventHandler<ActionEvent> getRefreshEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getList();
            }
        };
    }

    public void getList(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://localhost:3000/ListePluginjava");
        HttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httpGet);

        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity entity1 = response1.getEntity();

        InputStream is = null;
        try {
            is = entity1.getContent();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String filePath = "Pluginslist/plugin.json";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int inByte;
        try {
            if (is != null) {
                while((inByte = is.read()) != -1) {
                    try {
                        if (fos != null) {
                            fos.write(inByte);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (is != null) {
                is.close();
            }

            if (fos != null) {
                fos.close();
            }


            EntityUtils.consume(entity1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            readPlugin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readPlugin() throws IOException {
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(
                    path));

            JSONArray jsonArray = (JSONArray) obj;

            for (Object JsonItem : jsonArray) {
                JSONObject jsonObject = (JSONObject) JsonItem;
                pluginList.add(new Plugin(jsonObject.get("name").toString(), jsonObject.get("author").toString(), jsonObject.get("created_at").toString()));
            }

            for (Plugin plugin : pluginList) {
                System.out.println(plugin.getName());
            }

        } catch (Exception e) {
=======
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
>>>>>>> f1480a5ae66437a3e7ba8b3fb22a0d69e9e7bfaa
            e.printStackTrace();
        }
    }
}
