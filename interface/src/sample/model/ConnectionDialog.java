package sample.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.scene.control.*;
import sample.Main;

import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * Class of creation of the connection dialog box.
 */
public class ConnectionDialog {

    private Dialog<Pair<String, Integer>> dialog;
    private HBox content;
    private TextField addr1;
    private TextField addr2;
    private TextField addr3;
    private TextField addr4;
    private TextField port;
    private ButtonType validateButton;

    public ConnectionDialog() {

        dialog = new Dialog<Pair<String, Integer>>();
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
        port = addPortFilter("#port");

        // Bind changes on textfields with control function
        Node button = dialog.getDialogPane().lookupButton(validateButton);
        button.setDisable(false);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(getResultCallback());
    }

    public Dialog<Pair<String, Integer>> getDialog() {
        return dialog;
    }

    private TextField addTextFilter(String id) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String strValue = change.getControlNewText();
            boolean hide = false;
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

    private TextField addPortFilter(String id) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String strValue = change.getControlNewText();
            boolean hide = false;
            if(strValue.matches("[0-9]+")) {
                int value = Integer.parseInt(strValue);
                if(strValue.length() > 1 && value == 0) {
                    return null;
                }
                if(value >= 0 && value <= 65535) {
                    return change;
                }
            }

            return null;
        };

        TextField field = (TextField) this.content.lookup(id);
        field.setTextFormatter(new TextFormatter<>(filter));

        return field;
    }

    private Callback<ButtonType, Pair<String, Integer>> getResultCallback() {
        // Return the values of the fields on submitButton event
        return (param) -> {
            // If the user clicked on the submit button
            if(param == validateButton) {
                String address = addr1.getText()+"."+addr2.getText()+"."+addr3.getText()+"."+addr4.getText();
                int portNb = -1;
                try {
                    portNb = Integer.parseInt(port.getText());
                } catch (NumberFormatException e) {
                }

                return new Pair<>(address, portNb);
            }

            return null;
        };
    }
}
