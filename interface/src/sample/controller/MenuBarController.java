package sample.controller;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import sample.model.ConnectionDialog;

import java.io.IOException;

/**
 * Created by Vincent on 27/04/2016.
 */
public class MenuBarController {
    MenuBar menuBar;
    Menu mediacase;
    MenuItem openMedia;
    MenuItem openMediaAndAdd;
    Menu connection;
    MenuItem setConnection;

    public MenuBarController(AnchorPane root){
        menuBar = (MenuBar) root.lookup("#menuBar");
        mediacase = menuBar.getMenus().get(0);
        openMedia = mediacase.getItems().get(0);
        openMediaAndAdd = mediacase.getItems().get(1);
        connection = menuBar.getMenus().get(1);
        setConnection = connection.getItems().get(0);

        setConnection.setOnAction(event -> {
            try {
                ConnectionDialog connectionDialog = new ConnectionDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
