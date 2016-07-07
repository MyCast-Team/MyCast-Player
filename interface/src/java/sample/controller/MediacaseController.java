package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.json.simple.JSONObject;
import sample.model.Media;
import sample.model.Mediacase;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

import java.io.*;
import java.util.ArrayList;

import org.json.*;

/**
 * Created by Vincent on 14/06/2016.
 */
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

    private Mediacase mediacase;

    private Mediacase filteredMediacase;

    private DataFormat dataFormat;

    private static final String[] EXTENSIONS_AUDIO = {
            "3ga",
            "669",
            "a52",
            "aac",
            "ac3",
            "adt",
            "adts",
            "aif",
            "aifc",
            "aiff",
            "amb",
            "amr",
            "aob",
            "ape",
            "au",
            "awb",
            "caf",
            "dts",
            "flac",
            "it",
            "kar",
            "m4a",
            "m4b",
            "m4p",
            "m5p",
            "mid",
            "mka",
            "mlp",
            "mod",
            "mpa",
            "mp1",
            "mp2",
            "mp3",
            "mpc",
            "mpga",
            "mus",
            "oga",
            "ogg",
            "oma",
            "opus",
            "qcp",
            "ra",
            "rmi",
            "s3m",
            "sid",
            "spx",
            "tak",
            "thd",
            "tta",
            "voc",
            "vqf",
            "w64",
            "wav",
            "wma",
            "wv",
            "xa",
            "xm"
    };

    private static final String[] EXTENSIONS_VIDEO = {
            "3g2",
            "3gp",
            "3gp2",
            "3gpp",
            "amv",
            "asf",
            "avi",
            "bik",
            "bin",
            "divx",
            "drc",
            "dv",
            "evo",
            "f4v",
            "flv",
            "gvi",
            "gxf",
            "iso",
            "m1v",
            "m2v",
            "m2t",
            "m2ts",
            "m4v",
            "mkv",
            "mov",
            "mp2",
            "mp2v",
            "mp4",
            "mp4v",
            "mpe",
            "mpeg",
            "mpeg1",
            "mpeg2",
            "mpeg4",
            "mpg",
            "mpv2",
            "mts",
            "mtv",
            "mxf",
            "mxg",
            "nsv",
            "nuv",
            "ogg",
            "ogm",
            "ogv",
            "ogx",
            "ps",
            "rec",
            "rm",
            "rmvb",
            "rpl",
            "thp",
            "tod",
            "ts",
            "tts",
            "txd",
            "vob",
            "vro",
            "webm",
            "wm",
            "wmv",
            "wtv",
            "xesc"
    };

    private static final String path = "./res/mediacase.json";

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
                success = true;
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
                success = true;
                for (File file:db.getFiles()) {
                    if(videoExtensionIsSupported(getExtension(file.getPath()))){
                        MediaPlayerFactory mpf = new MediaPlayerFactory();
                        MediaMeta metaInfo = mpf.getMediaMeta(file.getPath(), true);
                        Media media = new Media(file.getPath(), metaInfo.getTitle(), metaInfo.getArtist(), metaInfo.getLength(), metaInfo.getDate(), metaInfo.getGenre());
                        this.mediacase.addMedia(media, 1);
                        JSONObject object = new JSONObject();
                        object.put("type", "video");
                        object.put("title", metaInfo.getTitle()==null?"":metaInfo.getTitle());
                        object.put("artist", metaInfo.getArtist()==null?"":metaInfo.getTitle());
                        object.put("length", PlayerController.formatTime(metaInfo.getLength()));
                        object.put("date", metaInfo.getDate()==null?"":metaInfo.getTitle());
                        object.put("genre", metaInfo.getGenre()==null?"":metaInfo.getTitle());
                        jsonList.add(object);
                    }
                }
                if(!jsonList.isEmpty()){
                    writeMediacase(jsonList);
                }
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
                for (Media m : mediacase.getMusiccase()) {
                    if (m.getAuthor().toLowerCase().contains(filter) || m.getTitle().toLowerCase().contains(filter) || m.getGenre().toLowerCase().contains(filter)) {
                        filteredMediacase.addMedia(m, 1);
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
            for (Media m : mediacase.getMusiccase()) {
                    filteredMediacase.addMedia(m, 1);
            }
            refreshMediacase();
        });
    }

    public void writeMediacase(ArrayList<JSONObject> list){
        try {
            File file = new File(path);
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
            FileOutputStream fileOut = new FileOutputStream(path, true);
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

    public boolean audioExtensionIsSupported(String extension){
        for(String str: EXTENSIONS_AUDIO){
            if(extension.compareTo(str) == 0){
                return true;
            }
        }
        return false;
    }

    public boolean videoExtensionIsSupported(String extension) {
        for (String str : EXTENSIONS_VIDEO) {
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
