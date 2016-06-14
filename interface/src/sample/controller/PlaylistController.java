package sample.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import sample.model.Media;
import sample.model.Playlist;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Class of control of the music.
 */
public class PlaylistController {
    @FXML
    private TableView<Media> musicTable;
    @FXML
    private TableColumn<Media, String> titleColumn;
    @FXML
    private TableColumn<Media, String> authorColumn;
    @FXML
    private TableColumn<Media, String> durationColumn;
    @FXML
    private Button reset;

    private Playlist playlist;

    private MediaListPlayer mediaListPlayer;

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

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PlaylistController() {
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public MediaListPlayer getMediaListPlayer() {
        return mediaListPlayer;
    }

    public void setMediaListPlayer(MediaListPlayer mediaListPlayer) {
        this.mediaListPlayer = mediaListPlayer;
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

        titleColumn.prefWidthProperty().bind(musicTable.widthProperty().divide(2));
        authorColumn.prefWidthProperty().bind(musicTable.widthProperty().divide(4));
        durationColumn.prefWidthProperty().bind(musicTable.widthProperty().divide(4));

        refreshPlaylist();
        setDragAndDrop();

        reset.setOnAction(event -> {
            this.playlist.reset();
            this.mediaListPlayer.stop();
            this.mediaListPlayer.getMediaList().clear();
            refreshPlaylist();
        });

        musicTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete from playlist");
        delete.setOnAction(event1 -> {
            ObservableList<Media> listToDelete = musicTable.getSelectionModel().getSelectedItems();
            for(Media mToDelete : listToDelete){
                Iterator<Media> iter = playlist.getPlaylist().iterator();

                while (iter.hasNext()) {
                    Media m = iter.next();

                    if (m == mToDelete){
                        iter.remove();
                    }

                }
            }

            this.refreshPlaylist();
        });
        contextMenu.getItems().addAll(delete);

        musicTable.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(musicTable, event.getScreenX(), event.getScreenY());
            }
        });

        musicTable.setOnMousePressed(event -> {
           if (event.isPrimaryButtonDown() && event.getClickCount() == 2){
               mediaListPlayer.playItem(musicTable.getSelectionModel().getSelectedIndex());
           }
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
                        this.playlist.addMedia(new Media(file.getPath(), metaInfo.getTitle(), metaInfo.getArtist(), metaInfo.getLength(), metaInfo.getDate(), metaInfo.getGenre()));
                        this.mediaListPlayer.getMediaList().addMedia(file.getPath());
                    }
                }
            } else {
                DataFormat dataFormat = null;
                for (DataFormat df : db.getContentTypes()) {
                    dataFormat = df;
                }
                if(dataFormat != null){
                    ArrayList<Media> list = (ArrayList<Media>) db.getContent(dataFormat);
                    for (Media m: list){
                        this.playlist.addMedia(m);
                        this.mediaListPlayer.getMediaList().addMedia(m.getPath());
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

        for(String str: EXTENSIONS_VIDEO){
            if(extension.compareTo(str) == 0){
                return true;
            }
        }
        return false;
    }

    public void refreshPlaylist(){
        ObservableList<Media> list = FXCollections.observableArrayList(playlist.getPlaylist());
        musicTable.setItems(list);
        this.playlist.writePlaylist();
    }
}
