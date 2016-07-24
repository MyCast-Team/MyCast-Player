package sample.model;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.MenuItem;
import sample.annotation.DocumentationAnnotation;
import sample.utility.AlertManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by thomasfouan on 18/07/2016.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date="18/07/2016", description = "This class is used in the StreamMedia class to receive message from the client.")
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
                    disconnect();
                }
            }
        } catch (IOException e) {
            if(socket != null && !socket.isClosed()) {
                disconnect();
            }
        }
    }

    /**
     * Disconnect the application from the client when the client is unavailable
     */
    private void disconnect() {
        // Fire an ActionEvent on the setConnection/Disconnect MenuItem
        Platform.runLater(() -> {
            Event.fireEvent(setConnection, new ActionEvent(null, setConnection));
            new AlertManager(StreamMedia.class, -1);
        });
    }
}
