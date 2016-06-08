package sample.controller;

import javafx.fxml.FXML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.model.Music;
import sample.model.Playlist;

/**
 * Class of control of the music.
 */
public class MusicController {
    @FXML
    private TableView<Music> musicTable;
    @FXML
    private TableColumn<Music, String> titleColumn;
    @FXML
    private TableColumn<Music, String> authorColumn;
    @FXML
    private TableColumn<Music, String> durationColumn;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public MusicController() {
    }

    /**
     * Initializes the sample.controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     */
    public void setPlaylist(Playlist playlist) {
        // Add observable list data to the table
        ObservableList<Music> list = FXCollections.observableArrayList(playlist.getPlaylist());
        musicTable.setItems(list);
    }
}
