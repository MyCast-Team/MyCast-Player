package sample.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
<<<<<<< HEAD
import sample.model.Music;
=======
>>>>>>> 0fb205fae293845fed3c27fbd00c477a46b3cc3e
import sample.Main;

/**
 * Class of control of the music.
 */
public class MusicController {
<<<<<<< HEAD
    @FXML
    private TableView<Music> musicTable;
=======
    /*@FXML
    private TableView<MP3Music> musicTable;
>>>>>>> 0fb205fae293845fed3c27fbd00c477a46b3cc3e
    @FXML
    private TableColumn<Music, String> titleColumn;
    @FXML
    private TableColumn<Music, String> authorColumn;
    @FXML
<<<<<<< HEAD
    private TableColumn<Music, String> lengthColumn;
=======
    private TableColumn<MP3Music, String> lengthColumn;*/
>>>>>>> 0fb205fae293845fed3c27fbd00c477a46b3cc3e

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
<<<<<<< HEAD
        musicTable.setItems((ObservableList<Music>) main.getMusicData());
=======
        /*musicTable.setItems(main.getMusicData());*/
>>>>>>> 0fb205fae293845fed3c27fbd00c477a46b3cc3e
    }
}
