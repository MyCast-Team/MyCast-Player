import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.scene.control.*;
import sun.applet.Main;

import java.io.IOException;
import java.util.function.UnaryOperator;

/**
 * Created by Vincent on 26/04/2016.
 */
public class ConnectionDialog {

    private Dialog<Pair<String, Integer>> dialog;
    private TextField addr1;
    private TextField addr2;
    private TextField addr3;
    private TextField addr4;
    private TextField port;
    private ButtonType validateButton;

    public ConnectionDialog() {
        // Load the body of the Dialog box from fxml
        FXMLLoader dialogLoad = new FXMLLoader();
        dialogLoad.setLocation(Main.class.getResource("/views/connection.fxml"));
        HBox content = null;
        try {
            content = (HBox) dialogLoad.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UnaryOperator<TextFormatter.Change> textFilter = getTextFilter();

        // Get the textFields of the dialog box and add a textFormatter to each of them
        this.addr1 = (TextField) content.lookup("#addr1");
        this.addr1.setTextFormatter(new TextFormatter<>(textFilter));
        this.addr2 = (TextField) content.lookup("#addr2");
        this.addr2.setTextFormatter(new TextFormatter<>(textFilter));
        this.addr3 = (TextField) content.lookup("#addr3");
        this.addr3.setTextFormatter(new TextFormatter<>(textFilter));
        this.addr4 = (TextField) content.lookup("#addr4");
        this.addr4.setTextFormatter(new TextFormatter<>(textFilter));
        this.port = (TextField) content.lookup("#port");
        this.port.setTextFormatter(new TextFormatter<>(getPortFilter()));

        this.dialog = new Dialog<Pair<String, Integer>>();
        this.dialog.setTitle("Connection to the client");
        this.dialog.setHeaderText("Enter the IP address of the client");

        this.validateButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        this.dialog.getDialogPane().getButtonTypes().addAll(validateButton, ButtonType.CANCEL);

        // Bind changes on textfields with control function
        Node button = this.dialog.getDialogPane().lookupButton(validateButton);
        button.setDisable(false);

        this.dialog.getDialogPane().setContent(content);

        this.dialog.setResultConverter(getResultCallback());
    }

    public Dialog getDialog() {
        return dialog;
    }

    private UnaryOperator<TextFormatter.Change> getTextFilter() {
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

        return filter;
    }

    private UnaryOperator<TextFormatter.Change> getPortFilter() {
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

        return filter;
    }

    private Callback<ButtonType, Pair<String, Integer>> getResultCallback() {
        // Return the values of the fields on submitButton event
        Callback<ButtonType, Pair<String, Integer>> callback = new Callback<ButtonType, Pair<String, Integer>>() {
            @Override
            public Pair<String, Integer> call(ButtonType param) {
                // If the user clicked on the submit button
                if(param == validateButton) {
                    String address = addr1.getText()+"."+addr2.getText()+"."+addr3.getText()+"."+addr4.getText();
                    int portNb = -1;
                    try {
                        portNb = Integer.parseInt(port.getText());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return new Pair<String, Integer>(address, portNb);
                }

                return null;
            }
        };

        return callback;
    }
}
