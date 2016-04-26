package sample.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import javafx.scene.control.*;
import sample.Main;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by Vincent on 26/04/2016.
 */
public class ConnectionDialog {

    void ConnectionDialog() throws IOException {
        Dialog <Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Connection to the client");
        dialog.setHeaderText("Enter the IP address of the client");

        ButtonType validateButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(validateButton, ButtonType.CANCEL);

        FXMLLoader dialogLoad = new FXMLLoader();
        dialogLoad.setLocation(Main.class.getResource("view/connection.fxml"));
        dialog.getDialogPane().setContent(dialogLoad.load());

        dialog.showAndWait();
    }
}
