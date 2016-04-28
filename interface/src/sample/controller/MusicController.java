package sample.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.model.Music;
import sample.Main;

/**
 * Class of control of the music.
 */
public class MusicController {
    /*@FXML
    private TableView<MP3Music> musicTable;
    @FXML
    private TableColumn<Music, String> titleColumn;
    @FXML
    private TableColumn<Music, String> authorColumn;
    @FXML
    private TableColumn<MP3Music, String> lengthColumn;*/

    // Reference to the main application.
    private Main main;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public MusicController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        /*titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        lengthColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());*/
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param main
     */
    public void setMain(Main main) {
        this.main = main;
        // Add observable list data to the table
        /*musicTable.setItems(main.getMusicData());*/
    }
}
