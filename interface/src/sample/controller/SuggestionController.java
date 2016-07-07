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
        HttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httppost);

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

        String filePath = "./res/id.json";
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
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("./res/id.json"));

            JSONObject jsonObject = (JSONObject) obj;


            id=jsonObject.get("id").toString();
            System.out.println(id);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void getList(String path,String http){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://localhost:3000/1/"+http);
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

        String filePath = "./res/"+path;
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
            readPlugin(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readPlugin(String path) throws IOException {
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("./res/"+path));

            JSONArray jsonArray = (JSONArray) obj;

            for (Object JsonItem : jsonArray) {
                JSONObject jsonObject = (JSONObject) JsonItem;
                //System.out.println(jsonObject.get("film").toString());
                if (path == "filmuser.json") {
                    SuggestionList.add(new Suggestion(jsonObject.get("film").toString(), jsonObject.get("director").toString(), jsonObject.get("length").toString(), jsonObject.get("date").toString(),jsonObject.get("type").toString(),"NULL"));
                }else{

                    System.out.println(jsonObject.get("type").toString());
                    SuggestionMusicList.add(new Suggestion(jsonObject.get("title").toString(), jsonObject.get("singer").toString(), jsonObject.get("length").toString(),jsonObject.get("date").toString(),jsonObject.get("type").toString(),jsonObject.get("producer").toString()));
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
