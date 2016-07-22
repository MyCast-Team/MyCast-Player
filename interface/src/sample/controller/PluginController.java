package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
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

import java.io.*;
import java.util.ArrayList;

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
    @FXML
    private Button search;
    @FXML
    private Button reset;
    @FXML
    private TextField filter;

    private ArrayList<Plugin> pluginList;

    private ArrayList<Plugin> filteredPluginList;

    public PluginController(){
    }

    @FXML
    public void initialize(){
        pluginList = new ArrayList<>();
        filteredPluginList = new ArrayList<>();

        getList();

        ObservableList<Plugin> list = FXCollections.observableArrayList(filteredPluginList);
        pluginTable.setItems(list);

        nameColumn1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        authorColumn1.setCellValueFactory(cellData -> cellData.getValue().AuthorProperty());
        dateColumn1.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        idColumn1.setCellValueFactory(cellData->cellData.getValue().IdProperty());

        refresh.setOnAction(getRefreshEventHandler());
        download.setOnAction(getDownloadEventHandler());
        remove.setOnAction(getRemoveEventHandler());
        pluginTable.getSelectionModel().selectedItemProperty().addListener(getSelectedItemChangeListener());

        search.setOnAction(getSearchEventHandler());
        reset.setOnAction(getResetEventHandler());

        installTooltips();
    }

    /**
     * Return an EventHandler for the Search button
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getSearchEventHandler() {
        return (event) -> {
            if (!filter.getText().equals("")) {
                String filterString = filter.getText().toLowerCase();
                filteredPluginList.clear();
                for (Plugin p : pluginList) {
                    if (p.getAuthor().toLowerCase().contains(filterString) ||
                            p.getDate().toLowerCase().contains(filterString) ||
                            p.getName().toLowerCase().contains(filterString)) {
                        filteredPluginList.add(p);
                    }
                }
                refreshPlugin();
            }
        };
    }

    /**
     * Return an EventHandler for the Reset button
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getResetEventHandler() {
        return (event) -> {
            filteredPluginList.clear();
            for (Plugin p : pluginList) {
                filteredPluginList.add(p);
            }
            refreshPlugin();
        };
    }

    /**
     * Return an EventHandler for the Refresh button
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getRefreshEventHandler() {
        return (event) -> {
            pluginList.clear();
            getList();
            ObservableList<Plugin> list = FXCollections.observableArrayList(filteredPluginList);

            pluginTable.setItems(list);
        };
    }

    /**
     * Return an EventHandler for the Download button
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getDownloadEventHandler() {
        return (event) -> {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constant.SERVER_ADDRESS+"/getpluginjava/"+ pluginTable.getSelectionModel().selectedItemProperty().getValue().getId());
            HttpResponse response1;
            HttpEntity entity1;
            InputStream is;
            String filePath = Constant.PATH_TO_PLUGIN + "/" + pluginTable.getSelectionModel().selectedItemProperty().getValue().getName();
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
                alert(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * Return an EventHandler for the Remove button
     * @return EventHandler
     */
    private EventHandler<ActionEvent> getRemoveEventHandler() {
        return (event) -> {
            File f1 = new File(Constant.PATH_TO_PLUGIN + "/" + pluginTable.getSelectionModel().selectedItemProperty().getValue().getName());
            boolean success = f1.delete();

            if (!success) {
                alert(-1);
            } else {
                alert(2);
            }
            getRefreshEventHandler();
        };
    }

    /**
     * Show an alert accordingly of the type of the alert
     * @param type
     */
    public void alert(int type){
        Alert alert;
        if(type > 0){
            alert = new Alert(Alert.AlertType.INFORMATION);
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
        }
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Plugin message");
        switch (type) {
            case -1:
                alert.setHeaderText("Plugin not deleted");
                alert.setContentText("An error occurred, the plugin was not deleted !");
                break;
            case 1:
                alert.setHeaderText("Plugin downloaded");
                alert.setContentText("The plugin was downloaded without trouble ! You can now configure where you want to place it via Interface -> Configure interface.");
                break;
            case 2:
                alert.setHeaderText("Plugin deleted");
                alert.setContentText("The plugin was deleted without trouble !");
        }
        alert.showAndWait();
    }

    /**
     * Return a ChangeListener on the plugin table
     * @return ChangeListener
     */
    private ChangeListener<Plugin> getSelectedItemChangeListener() {
        return (ObservableValue<? extends Plugin> observable, Plugin oldValue, Plugin newValue) -> {
            Plugin selectedPlugin = newValue;
            String nameplugin = selectedPlugin.getName();
            String path0 = Constant.PATH_TO_PLUGIN + "/" + nameplugin;
            File theDir = new File(path0);

            // if the directory does not exist, set download on true and remove on false
            if (theDir.exists()) {
                remove.setDisable(false);
                download.setDisable(true);
            } else {
                download.setDisable(false);
                remove.setDisable(true);
            }
        };
    }

    /**
     * Set tooltips on each button
     */
    private void installTooltips(){
        this.refresh.setTooltip(new Tooltip("Refresh the plugin list"));
        this.remove.setTooltip(new Tooltip("Remove the selected plugin if it's installed"));
        this.download.setTooltip(new Tooltip("Download the selected plugin if it's not installed"));
    }

    /**
     * Get the list of plugin from the server
     */
    private void getList(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constant.SERVER_ADDRESS+"/Listepluginjava");
        HttpResponse response1;
        HttpEntity entity1;
        InputStream is;
        FileOutputStream fos;

        try {
            response1 = httpclient.execute(httpGet);
            entity1 = response1.getEntity();
            is = entity1.getContent();

            fos = new FileOutputStream(new File(Constant.PATH_TO_PLUGIN_FILE));

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

        filteredPluginList.addAll(pluginList);
    }

    /**
     * Add each plugin received from the server in the table view
     */
    private void readPlugin() {
        JSONParser parser = new JSONParser();
        Object obj;
        JSONArray jsonArray;

        try {
            obj = parser.parse(new FileReader(Constant.PATH_TO_PLUGIN_FILE));
            jsonArray = (JSONArray) obj;

            JSONObject jsonObject;
            for (Object JsonItem : jsonArray) {
                jsonObject = (JSONObject) JsonItem;
                pluginList.add(new Plugin(jsonObject.get("name").toString(), jsonObject.get("author").toString(), jsonObject.get("created_at").toString(),jsonObject.get("id").toString()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresh the plugin table view
     */
    private void refreshPlugin(){
        ObservableList<Plugin> list = FXCollections.observableArrayList(filteredPluginList);
        pluginTable.setItems(list);
    }
}
