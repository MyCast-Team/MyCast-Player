package sample.controller;

import javafx.fxml.FXML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import sample.model.Music;
import sample.model.Playlist;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

import java.io.File;

/**
 * Class of control of the music.
 */
public class PlaylistController {
    @FXML
    private TableView<Music> musicTable;
    @FXML
    private TableColumn<Music, String> titleColumn;
    @FXML
    private TableColumn<Music, String> authorColumn;
    @FXML
    private TableColumn<Music, String> durationColumn;
    @FXML
    private Button reset;

    private Playlist playlist;

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

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PlaylistController() {
    }

    /**
     * Initializes the sample.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        this.playlist = new Playlist();
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        refreshPlaylist();
        setDragAndDrop();

        reset.setOnAction(event -> {
            this.playlist.reset();
            refreshPlaylist();
        });
    }

    public void setDragAndDrop(){
        musicTable.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        musicTable.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                for (File file:db.getFiles()) {
                    if(extensionIsSupported(getExtension(file.getPath()))){
                        MediaPlayerFactory mpf = new MediaPlayerFactory();
                        MediaMeta metaInfo = mpf.getMediaMeta(file.getPath(), true);
                        this.playlist.addMedia(new Music(metaInfo.getTitle(), metaInfo.getArtist(), metaInfo.getLength()));
                    }
                }
            }
            refreshPlaylist();
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    public boolean extensionIsSupported(String extension){
        for(String str: EXTENSIONS_AUDIO){
            if(extension.compareTo(str) == 0){
                return true;
            }
        }
        return false;
    }

    public void refreshPlaylist(){
        ObservableList<Music> list = FXCollections.observableArrayList(playlist.getPlaylist());
        musicTable.setItems(list);
        this.playlist.writePlaylist();
    }
}
