package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.model.Suggestion;
import sample.utility.Utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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

    private ArrayList<Suggestion> suggestionList;
    private ArrayList<Suggestion> suggestionMusicList;
    private String id;

    public SuggestionController() {
    }

    @FXML
    public void initialize() {
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject jsonObject;

        try {
            obj = parser.parse(new FileReader(Constant.PATH_TO_ID));
            jsonObject = (JSONObject) obj;
            id = jsonObject.get("id").toString();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            id = null;
        } finally {
            if (id == null) {
                id = generateId();
            }
        }

        suggestionList = new ArrayList<>();
        suggestionMusicList = new ArrayList<>();
        getList(Constant.PATH_TO_SUGGESTED_FILM, "ListeFilm");
        getList(Constant.PATH_TO_SUGGESTED_MUSIC, "ListeMusique");
        ObservableList<Suggestion> list = FXCollections.observableArrayList(suggestionList);
        ObservableList<Suggestion> listMusic = FXCollections.observableArrayList(suggestionMusicList);

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

    /**
     * Generate an id for the current user if it doesn't have one
     * @return id as String
     */
    public static String generateId() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Constant.SERVER_ADDRESS + "/addUser");
        HttpResponse response1;
        HttpEntity entity1;

        InputStream is;
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject jsonObject;

        try {
            response1 = httpclient.execute(httppost);
            entity1 = response1.getEntity();
            is = entity1.getContent();

            Utility.writeInFile(is, Constant.PATH_TO_ID);
            is.close();

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

    /**
     * Send the mediacase file to the server to update it about the music and film of the user
     */
    public static void sendData() {

        File file = new File(Constant.PATH_TO_MEDIACASE);
        HttpClient httpclient;
        HttpPost httppost;
        MultipartEntity mpEntity;
        ContentBody cbFile;
        HttpResponse response;
        HttpEntity resEntity;

        if(file.exists()) {
            httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            httppost = new HttpPost(Constant.SERVER_ADDRESS + "/mediacase");
            mpEntity = new MultipartEntity();
            cbFile = new FileBody(file);

            try {
                mpEntity.addPart("mediacase", cbFile);
                httppost.setEntity(mpEntity);

                response = httpclient.execute(httppost);
                resEntity = response.getEntity();

                if (resEntity != null) {
                    resEntity.consumeContent();
                }

                httpclient.getConnectionManager().shutdown();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    Files.delete(Paths.get(Constant.PATH_TO_MEDIACASE));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get a list of suggestion from the server and save it in a local file
     * @param path
     * @param http
     */
    private void getList(String path, String http) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constant.SERVER_ADDRESS + "/" + id + "/" + http);
        HttpResponse response1;
        HttpEntity entity1;
        InputStream is;

        try {
            response1 = httpclient.execute(httpGet);
            entity1 = response1.getEntity();
            is = entity1.getContent();

            Utility.writeInFile(is, path);
            is.close();

            EntityUtils.consume(entity1);
            readSuggestionFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read file from the path given in parameter, and fill the appropriate list
     * @param path
     */
    private void readSuggestionFile(String path) {

        JSONParser parser = new JSONParser();
        JSONArray jsonArray;
        JSONObject jsonObject;
        Suggestion suggestion;

        try {
            jsonArray = (JSONArray) parser.parse(new FileReader(path));

            for (Object JsonItem : jsonArray) {
                jsonObject = (JSONObject) JsonItem;
                suggestion = new Suggestion(null, jsonObject.get("date").toString(), null, jsonObject.get("length").toString(), jsonObject.get("type").toString());

                if (path.equals(Constant.PATH_TO_SUGGESTED_FILM)) {
                    suggestion.setName(jsonObject.get("film").toString());
                    suggestion.setDirector(jsonObject.get("director").toString());
                    suggestionList.add(suggestion);
                } else {
                    suggestion.setName(jsonObject.get("title").toString());
                    suggestion.setDirector(jsonObject.get("singer").toString());
                    suggestionMusicList.add(suggestion);
                }
            }
        } catch (ParseException | IOException e) {
            suggestionList.clear();
            suggestionMusicList.clear();
            e.printStackTrace();
        }
    }
}
