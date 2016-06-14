package sample.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import sample.Main;
import sample.controller.MainFrameController;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Created by thomasfouan on 14/06/16.
 */
public class InterfaceDialog {

    private Dialog dialog;
    private ScrollPane content;
    private ButtonType validateButton;
    private final String pathToPlugin = "/plugin";
    private final String[] staticInterfaces = {"Suggestions", "Playlist", "Player", "Plugin"};
    private HashMap<String, Point> currentInterface;

    public InterfaceDialog() {

        dialog = new Dialog<>();
        dialog.setTitle("Interface Configurator");
        dialog.setHeaderText("Configure your own interface with available panels");

        validateButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(validateButton, ButtonType.CANCEL);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/sample/view/interfaceDialog.fxml"));
        try {
            content = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentInterface = MainFrameController.readComponent();

        // Create a new line in the GridPane for each plugin found in the plugin folder
        getInterfaces();

        // Bind changes on textfields with control function
        Node button = dialog.getDialogPane().lookupButton(validateButton);
        button.setDisable(false);

        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(getResultCallback());

        dialog.showAndWait();
    }

    private void addTableRows(GridPane table, String name, int row) {
        ChoiceBox select = new ChoiceBox();
        select.getItems().addAll("None", "Top-left", "Top-right", "Bottom-left", "Bottom-right");

        Iterator it = currentInterface.entrySet().iterator();
        Entry<String, Point> entry;
        while(it.hasNext()) {
            entry = (Entry<String, Point>) it.next();
            if(entry.getKey().toLowerCase().contains(name.toLowerCase())) {
                int x = entry.getValue().getX();
                int y = entry.getValue().getY();

                if(x == 0 && y == 0) {
                    select.getSelectionModel().select(1);
                } else if(x == 1 && y == 0) {
                    select.getSelectionModel().select(2);
                } else if(x == 0 && y == 1) {
                    select.getSelectionModel().select(3);
                } else if(x == 1 && y == 1) {
                    select.getSelectionModel().select(4);
                }
            }
        }

        table.add(new Label(name), 0, row);
        table.add(select, 1, row);
    }

    private void getInterfaces() {

        GridPane table = (GridPane) content.getContent();

        for(int i=0; i<4; i++) {
            addTableRows(table, staticInterfaces[i], i+1);
        }

        Path path = Paths.get(pathToPlugin);
        if(Files.isDirectory(path)) {
            DirectoryStream<Path> ds = null;
            try {
                ds = Files.newDirectoryStream(path);

                Iterator<Path> it = ds.iterator();
                int count = 5;
                while(it.hasNext()) {
                    String filename = it.next().getFileName().toString();
                    if(filename.endsWith(".jar")) {
                        addTableRows(table, filename, count);
                        count++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Callback<ButtonType, Integer> getResultCallback() {
        Callback<ButtonType, Integer> callback = new Callback<ButtonType, Integer>() {
            @Override
            public Integer call(ButtonType param) {
                // If the user clicked on the submit button
                if(param == validateButton) {
                    // save the new configuration

                    return 1;
                }

                return null;
            }
        };

        return callback;
    }
}
