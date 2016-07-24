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
import sample.utility.Utility;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.medialist.MediaListItem;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Class of control of the music.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "25/04/2016", description = "This is the controller of the playlist component. The playlist is a list of medias that will be played in order. You can delete medias or reset the playlist. The playlist is saved in a file between two MyCast use.")
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
     * Initializes the sample.PlaylistController class. This method is automatically called
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

        this.reset.setTooltip(new Tooltip("Reset the actual playlist to empty playlist"));

        reset.setOnAction(event -> {
            this.playlist.reset();
            if(this.mediaListPlayer != null) {
                this.mediaListPlayer.stop();
                this.mediaListPlayer.getMediaList().clear();
            }
            if(this.streamingPlayer != null && this.streamingPlayer.getMediaList() != null) {
                this.streamingPlayer.stop();
                this.streamingPlayer.getMediaList().clear();
            }
            refreshPlaylist();
        });

        musicTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete from playlist");
        delete.setOnAction(event1 -> {
            ObservableList<Media> listToDelete = musicTable.getSelectionModel().getSelectedItems();
            playlist.getPlaylist().removeAll(listToDelete);
            ArrayList<String> pathToDelete = listToDelete.stream().map(Media::getPath).collect(Collectors.toCollection(ArrayList::new));

            if(mediaListPlayer != null) {
                deleteItemFromMediaList(mediaListPlayer.getMediaList(), pathToDelete);
            }
            if(streamingPlayer != null && streamingPlayer.getMediaList() != null) {
                deleteItemFromMediaList(streamingPlayer.getMediaList(), pathToDelete);
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

    /**
     * Set drag'n'drop functionality in the playlist to add media in it
     */
    private void setDragAndDrop() {
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
                    if(Utility.audioExtensionIsSupported(Utility.getExtension(file.getPath()))
                            || Utility.videoExtensionIsSupported(Utility.getExtension(file.getPath()))){
                        metaInfo = mpf.getMediaMeta(file.getPath(), true);
                        System.out.println(file.getPath());
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
                        System.out.println(m.getPath());
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

    /**
     * Refresh the playlist, for example after a drag'n'drop
     */
    private void refreshPlaylist() {
        ObservableList<Media> list = FXCollections.observableArrayList(playlist.getPlaylist());
        musicTable.setItems(list);
        this.playlist.writePlaylist();
    }

    /**
     * Delete all item present in list of path in the medialist of the player (vlcj)
     */
    private void deleteItemFromMediaList(MediaList mediaList, ArrayList<String> paths) {
        URL url;
        File file1;
        File file2;
        MediaListItem item;

        if(mediaList == null || paths == null)
            return;

        for (int i = 0; i < mediaList.items().size(); i++) {
            item = mediaList.items().get(i);
            for (String path : paths) {
                try {
                    url = new URL(item.mrl());
                    file1 = new File(url.toURI());
                    file2 = new File(path);
                    if (file1.getPath().equals(file2.getPath())) {
                        mediaList.removeMedia(i);
                        i--;
                        break;
                    }
                } catch (IOException | URISyntaxException e) {
                    if (item.mrl().equals(path)) {
                        mediaList.removeMedia(i);
                        i--;
                        break;
                    }
                }
            }
        }
    }
}
