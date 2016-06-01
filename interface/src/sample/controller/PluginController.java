package sample.controller;

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
        nameColumn1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        authorColumn1.setCellValueFactory(cellData -> cellData.getValue().AuthorProperty());
        dateColumn1.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        refresh.setOnAction(getRefreshEventHandler());
        this.pluginList= new ArrayList();
        ObservableList<Plugin> list = FXCollections.observableArrayList(pluginList);
        pluginTable.setItems(list);
    }
    public EventHandler<ActionEvent> getRefreshEventHandler() {
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getList();


            }
        };

        return handler;
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
            while((inByte = is.read()) != -1) {
                try {
                    fos.write(inByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            is.close();

            fos.close();


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
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject=(JSONObject) jsonArray.get(i);
                pluginList.add(new Plugin(jsonObject.get("name").toString(),jsonObject.get("author").toString(),jsonObject.get("created_at").toString()));
            }
            for(int i =0;i<pluginList.size();i++){

                System.out.println(pluginList.get(i).getName());
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
