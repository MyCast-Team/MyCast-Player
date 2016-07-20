package sample.model;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import sample.Main;
import sample.constant.Constant;
import sample.controller.MainFrameController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by thomasfouan on 14/06/16.
 */
public class InterfaceDialog {

    private Dialog dialog;
    private ScrollPane content;
    private GridPane table;
    private ButtonType validateButton;
    private HashMap<String, Point> currentInterface;

    /**
     * Constructor
     */
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
            table = (GridPane) content.getContent();

            // Get the interface stored in interface.csv
            currentInterface = MainFrameController.readComponent();
            // Make sure staticInterfaces are in the HashMap. Else, put in it.
            for(String inter : Constant.staticInterfaces) {
                currentInterface.putIfAbsent(inter, new Point(-1, -1));
            }
            getNewPlugins();

            // Create a new line in the GridPane for each interface/plugin in the interface HashMap
            addTableRows();

            // Bind changes on textfields with control function
            Node button = dialog.getDialogPane().lookupButton(validateButton);
            button.setDisable(false);

            dialog.getDialogPane().setContent(content);
            dialog.setResultConverter(getResultCallback());

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a row in the GridPane for each plugins.
     */
    private void addTableRows() {
        ChoiceBox select;
        Label label;
        int x, y, nbRow = 1;

        for (Entry<String, Point> entry : currentInterface.entrySet()) {
            label = new Label(entry.getKey().substring(entry.getKey().lastIndexOf("/")+1, entry.getKey().lastIndexOf(".")));
            // Save the entire name as ID to get it later
            label.setId(entry.getKey());

            select = new ChoiceBox();
            select.getItems().addAll(Position.NONE, Position.TOP_LEFT, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT);
            select.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

            x = entry.getValue().getX();
            y = entry.getValue().getY();
            if(x == 0 && y == 0) {
                select.getSelectionModel().select(Position.TOP_LEFT);
            } else if(x == 1 && y == 0) {
                select.getSelectionModel().select(Position.TOP_RIGHT);
            } else if(x == 0 && y == 1) {
                select.getSelectionModel().select(Position.BOTTOM_LEFT);
            } else if(x == 1 && y == 1) {
                select.getSelectionModel().select(Position.BOTTOM_RIGHT);
            } else {
                select.getSelectionModel().select(Position.NONE);
            }
            table.add(label, 0, nbRow);
            table.add(select, 1, nbRow);
            nbRow++;
        }
    }

    /**
     * Add new plugins in the interface HashMap (In fact, all plugins that a are not in the "interface.csv" file).
     */
    private void getNewPlugins() {
        Path path = Paths.get(Constant.pathToPlugin);
        if(Files.isDirectory(path)) {
            try {
                for (Path path1 : Files.newDirectoryStream(path)) {
                    String filename = path1.getFileName().toString();
                    // Add the plugin in the interface HashMap if the plugin is not in it.
                    if(filename.endsWith(".jar") && currentInterface.get(filename) == null) {
                        currentInterface.put(filename, new Point(-1, -1));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return a Callback for the validateButton of the DialogBox.
     * @return Callback
     */
    private Callback<ButtonType, Integer> getResultCallback() {
        return (param) -> {
            // If the user clicked on the submit button
            if(param == validateButton) {
                // save the new configuration
                updateInterface();
                saveInterface();
                return 1;
            }

            return null;
        };
    }

    /**
     * Return the node corresponding to the row and column given in parameters in the interface HashMap.
     * @param row
     * @param column
     * @return Node
     */
    private Node getNodeByIndex(int row, int column) {
        for(Node node : table.getChildren()) {
            if(node.isManaged() && node instanceof Label) {
                Label label = (Label) node;
                if (GridPane.getRowIndex(label) == row && GridPane.getColumnIndex(label) == column) {
                    return node;
                }
            } else if(node instanceof ChoiceBox){
                ChoiceBox box = (ChoiceBox) node;
                if (GridPane.getRowIndex(box) == row && GridPane.getColumnIndex(box) == column) {
                    return node;
                }
            }
        }

        return null;
    }

    /**
     * Update the interface HashMap with the new values.
     */
    private void updateInterface() {
        Label name;
        ChoiceBox pos;
        for(int i=0; i<currentInterface.size(); i++) {
            name = (Label) getNodeByIndex(i+1, 0);
            pos = (ChoiceBox) getNodeByIndex(i+1, 1);
            if(currentInterface.get(name.getId()) != null) {
                currentInterface.replace(name.getId(), getPointFromPosition((Position) pos.getSelectionModel().getSelectedItem()));
            }
        }
    }

    private Point getPointFromPosition(Position position) {
        switch (position) {
            case TOP_LEFT:
                return new Point(0, 0);
            case TOP_RIGHT:
                return new Point(1, 0);
            case BOTTOM_LEFT:
                return new Point(0, 1);
            case BOTTOM_RIGHT:
                return new Point(1, 1);
            default:
                return new Point(-1, -1);
        }
    }

    /**
     * Save the new position of interface in the "interface.csv" file.
     */
    private void saveInterface() {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(Constant.pathToInterfaceConf, false));

            for(Entry<String, Point> entry : currentInterface.entrySet()) {
                bw.write(entry.getKey()+";"+entry.getValue().getX()+";"+entry.getValue().getY());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ChangeListener getChangeListener() {
        return new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                ReadOnlyProperty rop = (ReadOnlyProperty) observable;
                SelectionModel sourceNode = (SelectionModel) rop.getBean();

                for(Node node : table.getChildren()) {
                    if (node instanceof ChoiceBox) {
                        ChoiceBox box = (ChoiceBox) node;

                        // If another ChoiceBox has already this new value, set the value of this ChoiceBox at None
                        if (GridPane.getColumnIndex(box) == 1
                                && box.getSelectionModel() != sourceNode
                                && box.getSelectionModel().getSelectedItem().equals(newValue)) {
                            box.getSelectionModel().select(0);
                        }
                    }
                }
            }
        };
    }

    enum Position {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}
