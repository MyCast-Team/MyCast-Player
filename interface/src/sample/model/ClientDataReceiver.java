package sample.model;

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

    private PrintWriter sendData;

    private StreamMedia.CONNECTION_STATUS status;

    private MediaList playlist;

    private MediaListPlayer mediaListPlayer;

    public ClientDataReceiver(Socket socket, PrintWriter sendData, StreamMedia.CONNECTION_STATUS status, MediaList mediaList, MediaListPlayer mediaListPlayer) throws IOException {

        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.sendData = sendData;
        this.status = status;
        this.playlist = mediaList;
        this.mediaListPlayer = mediaListPlayer;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            bufferedReader.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        int receivedData;
        super.run();

        try {
            while((receivedData = Integer.parseInt(bufferedReader.readLine())) != StreamMedia.REQUEST_CLIENT.DISCONNECTION.ordinal()) {
                if (receivedData == StreamMedia.REQUEST_CLIENT.DISCONNECTION.ordinal()) {
                    // Fire event on disconnection button
                    this.interrupt();
                }
            }
        } catch (IOException e) {
            // Fire event on disconnection button
            this.interrupt();
        }
    }
}
