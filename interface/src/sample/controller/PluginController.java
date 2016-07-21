package sample.controller;

import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Callback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.model.Plugin;
import sample.model.StreamMedia;
import sun.plugin2.util.PluginTrace;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Pierre on 30/05/2016.
 */
@DocumentationAnnotation(author = "Pierre Lochouarn", date = "30/05/2016", description = "This is the controller for the plugin panel. You can manage multiple things like search, download or uninstall plugins.")
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
    private TableColumn<Plugin,String> idColumn1;
    @FXML
    private Button refresh;
    @FXML
    private Button download;
    @FXML
    private Button remove;

    private ArrayList<Plugin> pluginList;

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
        idColumn1.setCellValueFactory(cellData->cellData.getValue().IdProperty());

        refresh.setOnAction(getRefreshEventHandler());
        download.setOnAction(getDownloadEventHandler());
        remove.setOnAction(getRemoveEventHandler());
        pluginTable.getSelectionModel().selectedItemProperty().addListener(getSelectedItemChangeListener());

        installTooltips();
    }

    public void installTooltips(){
        this.refresh.setTooltip(new Tooltip("Refresh the plugin list"));
        this.remove.setTooltip(new Tooltip("Remove the selected plugin if it's installed"));
        this.download.setTooltip(new Tooltip("Download the selected plugin if it's not installed"));
    }

    public EventHandler<ActionEvent> getRefreshEventHandler() {
        return (event) -> {
            pluginList.clear();
            getList();
            ObservableList<Plugin> list = FXCollections.observableArrayList(pluginList);

            pluginTable.setItems(list);
        };
    }


    public EventHandler<ActionEvent> getDownloadEventHandler() {
        return (event) -> {
            System.out.println( pluginTable.getSelectionModel().selectedItemProperty().getValue().getId());
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://backoffice-client.herokuapp.com/getpluginjava/"+ pluginTable.getSelectionModel().selectedItemProperty().getValue().getId());
            HttpResponse response1;
            HttpEntity entity1;
            InputStream is;
            String filePath = Constant.pathToPlugin + "/" + pluginTable.getSelectionModel().selectedItemProperty().getValue().getName();
            FileOutputStream fos = null;

            try {
                response1 = httpclient.execute(httpGet);
                entity1 = response1.getEntity();
                is = entity1.getContent();

                fos = new FileOutputStream(new File(filePath));

                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                if (is != null) {
                    while((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    is.close();

                }

                fos.close();
                EntityUtils.consume(entity1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public EventHandler<ActionEvent> getRemoveEventHandler() {
        return (event) -> {
            File f1 = new File(Constant.pathToPlugin + "/" + pluginTable.getSelectionModel().selectedItemProperty().getValue().getName());

            boolean success = f1.delete();

            if (!success){
                System.out.println("Deletion failed.");
                //System.exit(0);
            }
            else{
                System.out.println("File deleted.");
            }
            getRefreshEventHandler();
        };
    }

    private ChangeListener<Plugin> getSelectedItemChangeListener() {
        return (ObservableValue<? extends Plugin> observale, Plugin oldValue, Plugin newValue) -> {
            Plugin selectedPlugin = newValue;
            String nameplugin = selectedPlugin.getName();
            boolean present = false;

            String path0 = Constant.pathToPlugin + "/" + nameplugin;
            System.out.println(path0);
            File theDir = new File(path0);

            // if the directory does not exist, create it
            if (theDir.exists()) {
                remove.setDisable(false);
                download.setDisable(true);
            } else {
                download.setDisable(false);
                remove.setDisable(true);
            }
        };
    }

    public void getList(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://backoffice-client.herokuapp.com/Listepluginjava");
        HttpResponse response1;
        HttpEntity entity1;
        InputStream is;
        FileOutputStream fos;

        try {
            response1 = httpclient.execute(httpGet);
            entity1 = response1.getEntity();
            is = entity1.getContent();

            fos = new FileOutputStream(new File(Constant.pathToPluginFile));

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            if (is != null) {
                while((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                is.close();
            }

            fos.close();
            EntityUtils.consume(entity1);

            readPlugin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readPlugin() {
        JSONParser parser = new JSONParser();
        Object obj;
        JSONArray jsonArray;

        try {
            obj = parser.parse(new FileReader(Constant.pathToPluginFile));
            jsonArray = (JSONArray) obj;

            JSONObject jsonObject;
            for (Object JsonItem : jsonArray) {
                jsonObject = (JSONObject) JsonItem;
                pluginList.add(new Plugin(jsonObject.get("name").toString(), jsonObject.get("author").toString(), jsonObject.get("created_at").toString(),jsonObject.get("id").toString()));
            }

            for (Plugin plugin : pluginList) {
                System.out.println(plugin.getName());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
