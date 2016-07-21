package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.model.Media;
import sample.model.Mediacase;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Vincent on 14/06/2016.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "14/06/2016", description = "This is the controller of the mediacase component. The mediacase is the list of the user's medias. You can search in this list and drag and drop medias into the playlist")
public class MediacaseController {
    @FXML
    private TableView<Media> musiccaseTable;
    @FXML
    private TableView<Media> videocaseTable;
    @FXML
    private TableColumn<Media, String> titleMusic;
    @FXML
    private TableColumn<Media, String> authorMusic;
    @FXML
    private TableColumn<Media, String> lengthMusic;
    @FXML
    private TableColumn<Media, String> genreMusic;
    @FXML
    private TableColumn<Media, String> dateMusic;
    @FXML
    private TableColumn<Media, String> titleVideo;
    @FXML
    private TableColumn<Media, String> directorVideo;
    @FXML
    private TableColumn<Media, String> lengthVideo;
    @FXML
    private TableColumn<Media, String> genreVideo;
    @FXML
    private TableColumn<Media, String> dateVideo;
    @FXML
    private TextField search;
    @FXML
    private Button searchButton;
    @FXML
    private Button resetButton;
    @FXML
    private TextField search2;
    @FXML
    private Button searchButton2;
    @FXML
    private Button resetButton2;
    @FXML
    private TabPane tab;

    private Mediacase mediacase;

    private Mediacase filteredMediacase;

    private DataFormat dataFormat;

    public MediacaseController(){}

    public Mediacase getMediacase() { return mediacase; }

    @FXML
    public void initialize(){
        this.mediacase = new Mediacase();
        this.filteredMediacase = new Mediacase();

        dataFormat =  new DataFormat("ObservableList<Media>");

        titleMusic.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorMusic.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        lengthMusic.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        genreMusic.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        dateMusic.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        titleVideo.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        directorVideo.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        lengthVideo.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        genreVideo.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        dateVideo.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        setDragAndDrop();

        musiccaseTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        videocaseTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        installTooltips();

        setSearchManagement();

        refreshMediacase();
    }

    public void setDragAndDrop(){
        musiccaseTable.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        musiccaseTable.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            ArrayList<JSONObject> jsonList = new ArrayList<>();
            boolean success = false;
            if (db.hasFiles()) {
                String id = "";
                success = true;
                File idFile = new File(Constant.pathToId);
                if(idFile.exists()){
                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject obj = (JSONObject) parser.parse(new FileReader(idFile));
                        id = (String) obj.get("id");
                    } catch (IOException | ParseException e) {
                        id = SuggestionController.getid();
                    }
                } else {
                    id = SuggestionController.getid();
                }
                JSONObject idObj = new JSONObject();
                idObj.put("id", id);
                jsonList.add(idObj);
                for (File file:db.getFiles()) {
                    if(audioExtensionIsSupported(getExtension(file.getPath()))){
                        MediaPlayerFactory mpf = new MediaPlayerFactory();
                        MediaMeta metaInfo = mpf.getMediaMeta(file.getPath(), true);
                        Media media = new Media(file.getPath(), metaInfo.getTitle(), metaInfo.getArtist(), metaInfo.getLength(), metaInfo.getDate(), metaInfo.getGenre());
                        boolean found = false;
                        for(Media m : this.mediacase.getMusiccase()){
                            if(m.equals(media))
                                found = true;
                        }
                        if(!found){
                            this.mediacase.addMedia(media, 0);
                            JSONObject object = new JSONObject();
                            object.put("type", "audio");
                            object.put("title", metaInfo.getTitle()==null?"":metaInfo.getTitle());
                            object.put("artist", metaInfo.getArtist()==null?"":metaInfo.getTitle());
                            object.put("length", PlayerController.formatTime(metaInfo.getLength()));
                            object.put("date", metaInfo.getDate()==null?"":metaInfo.getTitle());
                            object.put("genre", metaInfo.getGenre()==null?"":metaInfo.getTitle());
                            jsonList.add(object);
                        }
                    }
                }
                if(!jsonList.isEmpty()){
                    writeMediacase(jsonList);
                }
            }
            filteredMediacase.reset();
            for (Media m : mediacase.getMusiccase()) {
                filteredMediacase.addMedia(m, 0);
            }
            refreshMediacase();
            event.setDropCompleted(success);
            event.consume();
        });

        musiccaseTable.setOnDragDetected(event -> {
            ArrayList<Media> list = new ArrayList<>();
            for(Media m: musiccaseTable.getSelectionModel().getSelectedItems()){
                list.add(m);
            }
            Dragboard db = musiccaseTable.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.put(dataFormat, list);
            db.setContent(content);
            event.consume();
        });

        videocaseTable.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        videocaseTable.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            ArrayList<JSONObject> jsonList = new ArrayList<>();
            boolean success = false;
            if (db.hasFiles()) {
                String id = "";
                success = true;
                File idFile = new File(Constant.pathToId);
                if(idFile.exists()){
                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject obj = (JSONObject) parser.parse(new FileReader(idFile));
                        id = (String) obj.get("id");
                    } catch (IOException | ParseException e) {
                        id = SuggestionController.getid();
                    }
                } else {
                    id = SuggestionController.getid();
                }
                JSONObject idObj = new JSONObject();
                idObj.put("id", id);
                jsonList.add(idObj);
                for (File file : db.getFiles()) {
                    if (videoExtensionIsSupported(getExtension(file.getPath()))) {
                        MediaPlayerFactory mpf = new MediaPlayerFactory();
                        MediaMeta metaInfo = mpf.getMediaMeta(file.getPath(), true);
                        Media media = new Media(file.getPath(), metaInfo.getTitle(), metaInfo.getArtist(), metaInfo.getLength(), metaInfo.getDate(), metaInfo.getGenre());
                        boolean found = false;
                        for(Media m : this.mediacase.getVideocase()){
                            if(m.equals(media))
                                found = true;
                        }
                        if(!found){
                            System.out.println("add !");
                            this.mediacase.addMedia(media, 1);
                            JSONObject object = new JSONObject();
                            object.put("type", "video");
                            object.put("title", metaInfo.getTitle() == null ? "" : metaInfo.getTitle());
                            object.put("artist", metaInfo.getArtist() == null ? "" : metaInfo.getTitle());
                            object.put("length", PlayerController.formatTime(metaInfo.getLength()));
                            object.put("date", metaInfo.getDate() == null ? "" : metaInfo.getTitle());
                            object.put("genre", metaInfo.getGenre() == null ? "" : metaInfo.getTitle());
                            jsonList.add(object);
                        }
                    }
                }
                if (!jsonList.isEmpty()) {
                    writeMediacase(jsonList);
                }
            }
            filteredMediacase.reset();
            for (Media m : mediacase.getVideocase()) {
                filteredMediacase.addMedia(m, 1);
            }
            refreshMediacase();
            event.setDropCompleted(success);
            event.consume();
        });

        videocaseTable.setOnDragDetected(event -> {
            ArrayList<Media> list = new ArrayList<>();
            for(Media m: videocaseTable.getSelectionModel().getSelectedItems()){
                list.add(m);
            }
            Dragboard db = videocaseTable.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.put(dataFormat, list);
            db.setContent(content);
            event.consume();
        });
    }

    public void installTooltips(){
        search.setTooltip(new Tooltip("Enter the filter for the music search in your mediacase"));
        search2.setTooltip(new Tooltip("Enter the filter for the video search in your mediacase"));
        searchButton.setTooltip(new Tooltip("Do the search process"));
        searchButton2.setTooltip(new Tooltip("Do the search process"));
        resetButton.setTooltip(new Tooltip("Reset the search filter"));
        resetButton2.setTooltip(new Tooltip("Reset the search filter"));
    }

    public void setSearchManagement() {
        searchButton.setOnAction(event -> {
            if (!search.getText().equals("")) {
                String filter = search.getText().toLowerCase();
                filteredMediacase.reset();
                for (Media m : mediacase.getMusiccase()) {
                    if (m.getAuthor().toLowerCase().contains(filter) || m.getTitle().toLowerCase().contains(filter) || m.getGenre().toLowerCase().contains(filter)) {
                        filteredMediacase.addMedia(m, 0);
                    }
                }
                refreshMediacase();
            }
        });

        resetButton.setOnAction(event -> {
            filteredMediacase.reset();
            for (Media m : mediacase.getMusiccase()) {
                filteredMediacase.addMedia(m, 0);
            }
            refreshMediacase();
        });

        searchButton2.setOnAction(event -> {
            if (!search2.getText().equals("")) {
                String filter = search2.getText().toLowerCase();
                filteredMediacase.reset();
                for (Media m : mediacase.getVideocase()) {
                    if (m.getAuthor().toLowerCase().contains(filter) || m.getTitle().toLowerCase().contains(filter) || m.getGenre().toLowerCase().contains(filter)) {
                        filteredMediacase.addMedia(m, 1);
                    }
                }
                refreshMediacase();
            }
        });

        resetButton2.setOnAction(event -> {
            filteredMediacase.reset();
            for (Media m : mediacase.getVideocase()) {
                filteredMediacase.addMedia(m, 1);
            }
            refreshMediacase();
        });

        tab.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER && tab.getSelectionModel().isSelected(0)){
                searchButton.fire();
            }
            if(event.getCode() == KeyCode.ENTER && tab.getSelectionModel().isSelected(1)){
                searchButton2.fire();
            }
        });
    }

    public void writeMediacase(ArrayList<JSONObject> list){
        try {
            File file = new File(Constant.pathToMediacase);
            if(!file.exists()){
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write("");
                    writer.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            FileOutputStream fileOut = new FileOutputStream(Constant.pathToMediacase, true);
            byte[] comma = ",".getBytes();
            boolean first = true;
            for(JSONObject object : list) {
                if(!first)
                    fileOut.write(comma);
                byte[] byteArray = object.toString().getBytes();
                fileOut.write(byteArray);
                first = false;
            }
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    public String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    public static boolean audioExtensionIsSupported(String extension){
        for(String str: Constant.EXTENSIONS_AUDIO){
            if(extension.compareTo(str) == 0){
                return true;
            }
        }
        return false;
    }

    public static boolean videoExtensionIsSupported(String extension) {
        for (String str : Constant.EXTENSIONS_VIDEO) {
            if (extension.compareTo(str) == 0) {
                return true;
            }
        }
        return false;
    }

    public void refreshMediacase(){
        ObservableList<Media> musiclist = FXCollections.observableArrayList(filteredMediacase.getMusiccase());
        ObservableList<Media> videolist = FXCollections.observableArrayList(filteredMediacase.getVideocase());
        musiccaseTable.setItems(musiclist);
        videocaseTable.setItems(videolist);
        this.mediacase.writeMediacase();
    }
}