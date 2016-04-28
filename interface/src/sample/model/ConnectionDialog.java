package sample.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.scene.control.*;
import sample.Main;
import sample.controller.ConnectionController;

import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * Class of creation of the connection dialog box.
 */
public class ConnectionDialog {

    private ConnectionController connectionController;
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
        dialogLoad.setLocation(Main.class.getResource("view/connection.fxml"));
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

        connectionController = new ConnectionController(addr1, addr2, addr3, addr4, port, validateButton);
        dialog.setResultConverter(connectionController.getResultCallback());
    }

    public Dialog getDialog() {
        return dialog;
    }

    private TextField addTextFilter(String id) {
        UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
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
            }
        };

        TextField field = (TextField) this.content.lookup(id);
        field.setTextFormatter(new TextFormatter<>(filter));

        return field;
    }

    private TextField addPortFilter(String id) {
        UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change change) {
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
            }
        };

        TextField field = (TextField) this.content.lookup(id);
        field.setTextFormatter(new TextFormatter<>(filter));

        return field;
    }
}
