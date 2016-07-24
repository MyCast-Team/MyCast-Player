package sample.model;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import sample.Main;
import sample.annotation.DocumentationAnnotation;
import sample.controller.MainFrameController;

import java.io.*;
import java.util.Map.Entry;

/**
 * Created by thomasfouan on 14/06/16.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date = "14/06/2016", description = "This is a complete GUI to manage the way component are displayed in our application.")
public class InterfaceDialog {

    private Dialog dialog;
    private ScrollPane content;
    private GridPane table;
    private ButtonType validateButton;

    /**
     * Constructor
     */
    public InterfaceDialog() {

        FXMLLoader loader = new FXMLLoader();
        dialog = new Dialog<>();
        Node button;

        dialog.setTitle("Interface Configurator");
        dialog.setHeaderText("Configure your own interface with available panels");

        validateButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(validateButton, ButtonType.CANCEL);

        loader.setLocation(Main.class.getResource("/sample/view/interfaceDialog.fxml"));
        try {
            content = loader.load();
            table = (GridPane) content.getContent();

            // Create a new line in the GridPane for each interface/plugin in availableComponents
            addTableRows();

            // Bind changes on textfields with control function
            button = dialog.getDialogPane().lookupButton(validateButton);
            button.setDisable(false);

            dialog.getDialogPane().setContent(content);
            dialog.setResultConverter(getResultCallback());

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a row in the GridPane for each plugin.
     */
    private void addTableRows() {
        ChoiceBox select;
        Label label;
        int x, y, nbRow = 1;

        for (Entry<String, Point> entry : MainFrameController.availableComponents.entrySet()) {
            label = new Label(getNameByType(entry.getKey()));
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
     * Return a Callback for the validateButton of the DialogBox.
     * @return Callback
     */
    private Callback<ButtonType, Integer> getResultCallback() {
        return (param) -> {
            // If the user clicked on the submit button
            if(param == validateButton) {
                // update the new configuration in availableComponents
                updateInterface();
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
     * Get the name of an interface or plugin
     * @param path
     * @return String
     */
    private String getNameByType(String path) {
        String result = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));

        if(path.endsWith(".fxml")) {
            result = result + " (Default)";
        }

        return result;
    }

    /**
     * Return a Point accordingly to a Position
     * @param position
     * @return Point
     */
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
     * Update the interface HashMap with the new values.
     */
    private void updateInterface() {
        Label name;
        ChoiceBox pos;
        Point newPoint;
        boolean isChanged = false;

        for (int i = 0; i < MainFrameController.availableComponents.size(); i++) {
            name = (Label) getNodeByIndex(i + 1, 0);
            pos = (ChoiceBox) getNodeByIndex(i + 1, 1);
            if (name != null && pos != null && MainFrameController.availableComponents.get(name.getId()) != null) {
                newPoint = getPointFromPosition((Position) pos.getSelectionModel().getSelectedItem());
                if (!newPoint.equals(MainFrameController.availableComponents.get(name.getId()))) {
                    MainFrameController.availableComponents.replace(name.getId(), newPoint);
                    isChanged = true;
                }
            }
        }

        if (isChanged) {
            MainFrameController.saveInterface();
            Main.loadMainFrameController();
        }
    }

    /**
     * Return a ChangeListener for the choicebox of the alert popup
     * @return ChangeListener
     */
    private ChangeListener getChangeListener() {
        return (observable, oldValue, newValue) -> {
            ReadOnlyProperty rop = (ReadOnlyProperty) observable;
            SelectionModel sourceNode = (SelectionModel) rop.getBean();

            // If another ChoiceBox has already this new value, set the value of this ChoiceBox at None
            table.getChildren().stream().filter(node -> node instanceof ChoiceBox).forEach(node -> {
                ChoiceBox box = (ChoiceBox) node;

                // If another ChoiceBox has already this new value, set the value of this ChoiceBox at None
                if (GridPane.getColumnIndex(box) == 1
                        && box.getSelectionModel() != sourceNode
                        && box.getSelectionModel().getSelectedItem().equals(newValue)) {
                    box.getSelectionModel().select(0);
                }
            });
        };
    }

    /**
     * Enumeration of the position of an interface in the application
     */
    enum Position {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}
