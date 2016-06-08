package sample.controller;

import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Class of control of the connection dialog box.
 */
public class ConnectionController {

    private TextField addr1;
    private TextField addr2;
    private TextField addr3;
    private TextField addr4;
    private TextField port;
    private ButtonType validateButton;

    public ConnectionController(TextField addr1, TextField addr2, TextField addr3, TextField addr4, TextField port, ButtonType validateButton) {
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.addr3 = addr3;
        this.addr4 = addr4;
        this.port = port;
        this.validateButton = validateButton;
    }

    public Callback<ButtonType, Pair<String, Integer>> getResultCallback() {
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
