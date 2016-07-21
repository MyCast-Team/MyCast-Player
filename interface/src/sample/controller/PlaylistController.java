package sample.controller;

import javafx.fxml.FXML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import sample.annotation.DocumentationAnnotation;
import sample.model.Media;
import sample.model.Playlist;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class of control of the music.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "25/04/2016", description = "This is the controller of the playlist component. The playlist is a list of medias that will be played in order. You can delete medias or reset the playlist. The playlist is saved in a file between two MyShare use.")
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

    private MediaListPlayer streamingPlayer;

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

    public void setStreamingPlayer(MediaListPlayer streamingPlayer) {
        this.streamingPlayer = streamingPlayer;
    }

    /**
     * Initializes the sample.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        this.playlist = new Playlist();
        System.out.println("eeeee");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());

        titleColumn.prefWidthProperty().bind(musicTable.widthProperty().divide(2));
        authorColumn.prefWidthProperty().bind(musicTable.widthProperty().divide(4));
        durationColumn.prefWidthProperty().bind(musicTable.widthProperty().divide(4));

        refreshPlaylist();
        setDragAndDrop();

        this.reset.setTooltip(new Tooltip("Reset the actual playlist to empty playlist"));

        reset.setOnAction(event -> {
            this.playlist.reset();
            if(this.mediaListPlayer != null) {
                this.mediaListPlayer.stop();
                this.mediaListPlayer.getMediaList().clear();
            }
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
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2){
                if(musicTable.getSelectionModel().getSelectedIndex() != -1 && mediaListPlayer != null)
                    mediaListPlayer.playItem(musicTable.getSelectionModel().getSelectedIndex());
            }
            if (event.isSecondaryButtonDown()) {
                contextMenu.show(musicTable, event.getScreenX(), event.getScreenY());
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
            MediaPlayerFactory mpf = new MediaPlayerFactory();
            MediaMeta metaInfo;

            if (db.hasFiles()) {
                success = true;
                for (File file:db.getFiles()) {
                    if(MediacaseController.audioExtensionIsSupported(getExtension(file.getPath()))
                            || MediacaseController.videoExtensionIsSupported(getExtension(file.getPath()))){
                        metaInfo = mpf.getMediaMeta(file.getPath(), true);
                        this.playlist.addMedia(new Media(file.getPath(), metaInfo.getTitle(), metaInfo.getArtist(), metaInfo.getLength(), metaInfo.getDate(), metaInfo.getGenre()));
                        if(this.mediaListPlayer != null) {
                            this.mediaListPlayer.getMediaList().addMedia(file.getPath());
                        }
                        if(this.streamingPlayer != null && this.streamingPlayer.getMediaList() != null) {
                            this.streamingPlayer.getMediaList().addMedia(file.getPath());
                        }
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
                        if(this.mediaListPlayer != null && this.mediaListPlayer.getMediaList() != null) {
                            this.mediaListPlayer.getMediaList().addMedia(m.getPath());
                        }
                        if(this.streamingPlayer != null && this.streamingPlayer.getMediaList() != null) {
                            this.streamingPlayer.getMediaList().addMedia(m.getPath());
                        }
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

    public void refreshPlaylist(){
        ObservableList<Media> list = FXCollections.observableArrayList(playlist.getPlaylist());
        musicTable.setItems(list);
        this.playlist.writePlaylist();
    }
}
