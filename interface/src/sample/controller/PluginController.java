package sample.controller;

import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TableColumn<Plugin,String> downloadColumn1;
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

        downloadColumn1.setCellFactory(new Callback<TableColumn<Plugin,String>, TableCell<Plugin,String>>() {

            @Override
            public TableCell<Plugin,String> call(TableColumn<Plugin,String> p) {
                    p.setCellValueFactory(cellData->cellData.getValue().IdProperty());
                   return new ButtonCell();
            }
        });
        downloadColumn1.setCellValueFactory(cellData->cellData.getValue().IdProperty());
        refresh.setOnAction(getRefreshEventHandler());
    }

    public EventHandler<ActionEvent> getRefreshEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pluginList.clear();
                getList();
                ObservableList<Plugin> list = FXCollections.observableArrayList(pluginList);

                pluginTable.setItems(list);
            }
        };
    }

    public void getList(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://localhost:3000/Listepluginjava");
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
                pluginList.add(new Plugin(jsonObject.get("name").toString(), jsonObject.get("author").toString(), jsonObject.get("created_at").toString(),jsonObject.get("id").toString()));
            }

            for (Plugin plugin : pluginList) {
                System.out.println(plugin.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class ButtonCell extends TableCell<Plugin,String> {
        final Button cellButton = new Button("Download");

        ButtonCell(){

            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){

                @Override
                public void handle(ActionEvent t) {
                    System.out.println( pluginTable.getSelectionModel().selectedItemProperty().getValue().getId());
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://localhost:3000/getpluginjava/"+ pluginTable.getSelectionModel().selectedItemProperty().getValue().getId());
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
                    String filePath = "Pluginslist/"+ pluginTable.getSelectionModel().selectedItemProperty().getValue().getName();
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


                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(String t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }
}
