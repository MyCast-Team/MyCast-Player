package sample.model;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.StageStyle;
import sample.controller.MenuBarController;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by thomasfouan on 18/07/2016.
 */
public class ClientDataReceiver extends Thread {

    private Socket socket;
    private BufferedReader bufferedReader;

    private MenuItem setConnection;

    public ClientDataReceiver(Socket socket, MenuItem setConnection) throws IOException {

        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.setConnection = setConnection;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        super.run();
        String data;

        try {
            // Wait forever for disconnection signal from client
            while((data = bufferedReader.readLine()) != null && !data.trim().equals("")) {
                if(Integer.parseInt(data) == StreamMedia.REQUEST_CLIENT.DISCONNECTION.ordinal()) {
                    // Fire an ActionEvent on the setConnection/Disconnect MenuItem
                    Platform.runLater(() -> {
                        Event.fireEvent(setConnection, new ActionEvent(null, setConnection));
                        alert();
                    });
                }
            }
        } catch (IOException e) {

            if(socket != null && !socket.isClosed()) {
                // Fire an ActionEvent on the setConnection/Disconnect MenuItem
                Platform.runLater(() -> {
                    Event.fireEvent(setConnection, new ActionEvent(null, setConnection));
                    alert();
                });
            }
        }
    }

    public void alert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Streaming message");
        alert.setHeaderText("Client disconnected");
        String s = "The connection has been lost with the client. Check if the client is still opened or check your network connection.";
        alert.setContentText(s);
        alert.show();
    }
}
