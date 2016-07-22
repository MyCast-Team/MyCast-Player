package sample.controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.model.Suggestion;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Pierre on 30/05/2016.
 */
@DocumentationAnnotation(author = "Pierre Lochouarn", date = "30/05/2016", description = "This is the controller for the suggestion panel. The application will give to the user some ideas about movies to watch and music to listen.")
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
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("./res/id.json"));

            JSONObject jsonObject = (JSONObject) obj;

            id = jsonObject.get("id").toString();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        if (id == null) {
            id = generateid();
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
        lengthColumn.setCellValueFactory(cellData->cellData.getValue().LengthProperty());
        dateColumn2.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        authorColumn.setCellValueFactory(cellData->cellData.getValue().DirectorProperty());

        musicTable1.setItems(listMusic);
        titleColumn1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn1.setCellValueFactory(cellData->cellData.getValue().TypeProperty());
        dateColumn1.setCellValueFactory(cellData -> cellData.getValue().DateProperty());
        lengthColumn1.setCellValueFactory(cellData->cellData.getValue().LengthProperty());
        authorColumn1.setCellValueFactory(cellData->cellData.getValue().DirectorProperty());
    }

    public static String generateid(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://backoffice-client.herokuapp.com/addUser");
        HttpResponse response1;
        HttpEntity entity1;

        FileOutputStream fos;
        InputStream is;
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject jsonObject;

        try {
            response1 = httpclient.execute(httppost);
            entity1 = response1.getEntity();
            is = entity1.getContent();

            fos = new FileOutputStream(new File(Constant.PATH_TO_ID));

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

            obj = parser.parse(new FileReader(Constant.PATH_TO_ID));
            jsonObject = (JSONObject) obj;

            return jsonObject.get("id").toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendData() throws IOException {
        File file=new File("./res/mediacase.json");
        if(file.exists()){
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost("http://backoffice-client.herokuapp.com/mediacase");
            try {
                MultipartEntity mpEntity = new MultipartEntity();
                ContentBody cbFile = new FileBody(file);

                mpEntity.addPart("mediacase", cbFile);

                httppost.setEntity(mpEntity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    resEntity.consumeContent();
                }

                httpclient.getConnectionManager().shutdown();
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Files.delete(Paths.get("./res/mediacase.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getList(String path,String http){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://backoffice-client.herokuapp.com/"+id+"/"+http);
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
        JSONArray jsonArray;

        try {
            jsonArray = (JSONArray) parser.parse(new FileReader("./res/"+path));

            JSONObject jsonObject;
            for (Object JsonItem : jsonArray) {
                jsonObject = (JSONObject) JsonItem;
                if (path.equals("filmuser.json")) {
                    SuggestionList.add(new Suggestion(jsonObject.get("film").toString(), jsonObject.get("date").toString(), jsonObject.get("director").toString(), jsonObject.get("length").toString(),jsonObject.get("type").toString()));
                } else {
                    SuggestionMusicList.add(new Suggestion(jsonObject.get("title").toString(), jsonObject.get("date").toString(), jsonObject.get("singer").toString(),jsonObject.get("length").toString(),jsonObject.get("type").toString()));
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
