package sample.controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sample.model.Suggestion;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Pierre on 30/05/2016.
 */
public class SuggestionController {
    @FXML
    private TableView<Suggestion> musicTable1;
    @FXML
    private TableView<Suggestion> filmtable1;
    @FXML
    private TableColumn<Suggestion, String> titleColumn1;
    @FXML
    private TableColumn<Suggestion, String> authorColumn1;
    @FXML
    private TableColumn<Suggestion, String> dateColumn1;
    @FXML
    private TableColumn<Suggestion,String> lengthColumn1;
    @FXML
    private TableColumn<Suggestion,String> typeColumn1;
    @FXML
    private TableColumn<Suggestion, String> titleColumn;
    @FXML
    private TableColumn<Suggestion, String> dateColumn2;
    @FXML
    private TableColumn<Suggestion,String> lengthColumn;
    @FXML
    private TableColumn<Suggestion,String> authorColumn;
    @FXML
    private TableColumn<Suggestion,String> typeColumn;

    private ArrayList<Suggestion> SuggestionList;
    private ArrayList<Suggestion> SuggestionMusicList;
    private String id;

    public SuggestionController(){

    }

    @FXML
    public void initialize(){
        /*JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("./res/id.json"));

            JSONObject jsonObject = (JSONObject) obj;

            id=jsonObject.get("id").toString();
            System.out.println(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (id == null) {
            getid();
        }
        SuggestionList = new ArrayList<>();
        SuggestionMusicList=new ArrayList<>();
        getList("filmuser.json","ListeFilm");
        getList("musiqueuser.json","ListeMusique");
        ObservableList<Suggestion> list = FXCollections.observableArrayList(SuggestionList);
        ObservableList<Suggestion> listMusic = FXCollections.observableArrayList(SuggestionMusicList);
        filmtable1.setItems(list);
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData->cellData.getValue().TypeProperty());
        dateColumn2.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        lengthColumn.setCellValueFactory(cellData->cellData.getValue().LengthProperty());
        authorColumn.setCellValueFactory(cellData->cellData.getValue().DirectorProperty());
        musicTable1.setItems(listMusic);
        titleColumn1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn1.setCellValueFactory(cellData->cellData.getValue().TypeProperty());
        dateColumn1.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        lengthColumn1.setCellValueFactory(cellData->cellData.getValue().LengthProperty());
        authorColumn1.setCellValueFactory(cellData->cellData.getValue().DirectorProperty());*/
    }

    public void getid(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://localhost:3000/addUser");
        HttpResponse response1;
        HttpEntity entity1;

        String filePath = "./res/id.json";
        FileOutputStream fos;
        InputStream is;
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject jsonObject;

        try {
            response1 = httpclient.execute(httppost);
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

            obj = parser.parse(new FileReader("./res/id.json"));
            jsonObject = (JSONObject) obj;

            id = jsonObject.get("id").toString();
            System.out.println(id);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getList(String path,String http){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://localhost:3000/1/"+http);
        HttpResponse response1;
        HttpEntity entity1;

        String filePath = "./res/"+path;
        FileOutputStream fos;
        InputStream is;

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
            readPlugin(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readPlugin(String path) {

        JSONParser parser = new JSONParser();
        Object obj;
        JSONArray jsonArray;

        try {
            obj = parser.parse(new FileReader("./res/"+path));
            jsonArray = (JSONArray) obj;

            JSONObject jsonObject;
            for (Object JsonItem : jsonArray) {
                jsonObject = (JSONObject) JsonItem;
                //System.out.println(jsonObject.get("film").toString());
                if (path.equals("filmuser.json")) {
                    SuggestionList.add(new Suggestion(jsonObject.get("film").toString(), jsonObject.get("director").toString(), jsonObject.get("length").toString(), jsonObject.get("date").toString(),jsonObject.get("type").toString(),"NULL"));
                } else {
                    System.out.println(jsonObject.get("type").toString());
                    SuggestionMusicList.add(new Suggestion(jsonObject.get("title").toString(), jsonObject.get("singer").toString(), jsonObject.get("length").toString(),jsonObject.get("date").toString(),jsonObject.get("type").toString(),jsonObject.get("producer").toString()));
                }
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
