package sample.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by thomasfouan on 18/06/16.
 */
public class StatusBarController {

    @FXML
    private Label leftContent;

    @FXML
    private Label centerContent;

    @FXML
    private Label rightContent;

    public StatusBarController() {
    }

    @FXML
    public void initialize() {
    }

    public Label getLeftContent() {
        return leftContent;
    }

    public Label getCenterContent() {
        return centerContent;
    }

    public Label getRightContent() {
        return rightContent;
    }

    public void setInformation(String info) {
        if(info == null) {
            leftContent.setText("");
        } else {
            leftContent.setText(info);
        }
    }

    public void setMusicTitle(String title) {
        if(title == null) {
            centerContent.setText("No Playing item");
        } else {
            centerContent.setText(title);
        }
    }

    public void setStatusConnection(String addr, int port) {
        if(addr == null || port == -1) {
            rightContent.setText("Not connected");
        } else {
            rightContent.setText("Connected with "+addr+":"+port);
        }
    }
}
