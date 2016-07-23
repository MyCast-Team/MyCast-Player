package sample.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.scene.control.*;
import sample.Main;
import sample.annotation.DocumentationAnnotation;

import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * Class of creation of the connection dialog box.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date = "10/05/2016", description = "This is a complete GUI for a connection dialog to connect to a distant client.")
public class ConnectionDialog {

    private Dialog<String> dialog;
    private HBox content;
    private TextField addr1;
    private TextField addr2;
    private TextField addr3;
    private TextField addr4;
    private ButtonType validateButton;

    public ConnectionDialog() {

        dialog = new Dialog<>();
        dialog.setTitle("Connection to the client");
        dialog.setHeaderText("Enter the IP address of the client");

        validateButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(validateButton, ButtonType.CANCEL);

        FXMLLoader dialogLoad = new FXMLLoader();
        dialogLoad.setLocation(Main.class.getResource("/sample/view/connection.fxml"));
        try {
            content = dialogLoad.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the textFields of the dialog box and add a textFormatter to each of them
        addr1 = addTextFilter("#addr1");
        addr2 = addTextFilter("#addr2");
        addr3 = addTextFilter("#addr3");
        addr4 = addTextFilter("#addr4");

        // Bind changes on textfields with control function
        Node button = dialog.getDialogPane().lookupButton(validateButton);
        button.setDisable(false);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(getResultCallback());
    }

    public Dialog<String> getDialog() {
        return dialog;
    }

    /**
     * Add filter on textfields to prevent user to type incorrect IP address
     * @param id
     * @return TextField
     */
    private TextField addTextFilter(String id) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String strValue = change.getControlNewText();

            if(strValue.matches("[0-9]+")) {
                int value = Integer.parseInt(strValue);
                if(strValue.length() > 1 && value == 0) {
                    return null;
                }
                if(value >= 0 && value <= 255) {
                    return change;
                }
            }

            return null;
        };

        TextField field = (TextField) this.content.lookup(id);
        field.setTextFormatter(new TextFormatter<>(filter));

        return field;
    }

    /**
     * Get a Callback for the dialog box
     * @return Callback
     */
    private Callback<ButtonType, String> getResultCallback() {
        // Return the values of the fields on submitButton event
        return (param) -> {
            // If the user clicked on the submit button
            if(param == validateButton) {
                return addr1.getText() + "." + addr2.getText() + "." + addr3.getText()+ "." + addr4.getText();
            }

            return null;
        };
    }
}
