package sample.model;

import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.util.Pair;
import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.utility.AlertManager;
import sample.utility.Utility;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Optional;

/**
 * Created by thomasfouan on 09/03/2016.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date = "09/03/2016", description = "This class is the streaming manager. It creates socket with a client to broadcast medias.")
public class StreamMedia extends Thread {

    private MediaPlayerFactory factory;
    private MediaListPlayer mediaListPlayer;
    private MediaList playlist;
    private Playlist interfacePlaylist;

    private Socket socket;
    private PrintWriter sendData;

    private CONNECTION_STATUS status;

    private ClientDataReceiver clientDataReceiver;

    private MenuItem setConnection;

    /**
     * CONSTRUCTOR
     */
    public StreamMedia(MenuItem setConnection) {
        this.setConnection = setConnection;

        factory = new MediaPlayerFactory();
        mediaListPlayer = factory.newMediaListPlayer();
        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
            @Override
            public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
                try {
                    // Wait few milliseconds to make sure the MediaListPlayer is ready for the stream
                    // before client starts receiving data
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendData.println(StreamMedia.REQUEST_CLIENT.STREAMING_STARTED.ordinal());
                sendData.flush();
            }
        });
        playlist = factory.newMediaList();

        status = CONNECTION_STATUS.DISCONNECTED;
        interfacePlaylist = null;
        clientDataReceiver = null;
        socket = null;
        sendData = null;
    }

    /**
     * GETTERS
     */
    public CONNECTION_STATUS getStatus() { return status; }

    public Socket getSocket() { return socket; }

    public MediaListPlayer getMediaListPlayer() { return mediaListPlayer; }

    /**
     * SETTERS
     */
    public void setInterfacePlaylist(Playlist playlist) {
        this.interfacePlaylist = playlist;
    }


    /**
     * Create and prepare a player for streaming.
     */
    public void prepareStreamingMedia() {
        String rtspStream = Utility.formatRtspStream(socket.getLocalAddress().getHostAddress(), Constant.STREAMING_PORT, "demo");
        playlist.setStandardMediaOptions(rtspStream,
                ":no-sout-rtp-sap",
                ":no-sout-standard-sap",
                ":sout-all",
                ":sout-keep");

        mediaListPlayer.setMediaList(playlist);
        if(interfacePlaylist != null) {
            for (Media m : interfacePlaylist.getPlaylist()) {
                this.playlist.addMedia(m.getPath());
            }
        }
    }

    /**
     * Start the streaming at the address and port set with the prepareStreamingMedia function.
     */
    public void startStreamingMedia() { mediaListPlayer.play(); }

    /**
     * Set pause to the player.
     */
    public void pauseStreamingMedia() {
        mediaListPlayer.pause();
    }

    /**
     * Set a connection with a client to stream media to.
     * @return true if the connection is set, false otherwise.
     */
    public boolean setClientConnection() {

        ConnectionDialog connectionDialog = new ConnectionDialog();
        Optional<String> result = connectionDialog.getDialog().showAndWait();
        status = CONNECTION_STATUS.DISCONNECTED;

        if (result.isPresent()) {
            String addr = result.get();

            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(addr, Constant.PORT), 1000);

                sendData = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
                prepareStreamingMedia();

                clientDataReceiver = new ClientDataReceiver(socket, setConnection);
                clientDataReceiver.start();

                status = CONNECTION_STATUS.CONNECTED;
                new AlertManager(StreamMedia.class, 1, socket.getInetAddress().getCanonicalHostName());
            } catch (UnknownHostException e) {
                new AlertManager(StreamMedia.class, -4);
            } catch (SocketTimeoutException | ConnectException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                }
                new AlertManager(StreamMedia.class, -3);
            } catch (IOException e) {
                new AlertManager(StreamMedia.class, -2);
            }
        }

        return status.equals(CONNECTION_STATUS.CONNECTED);
    }

    /**
     * Close the connection with the current client.
     */
    public void closeConnection() {

        if(status.equals(CONNECTION_STATUS.DISCONNECTED))
            return;

        try {
            sendData.println(StreamMedia.REQUEST_CLIENT.DISCONNECTION.ordinal());
            sendData.flush();

            if(clientDataReceiver != null && clientDataReceiver.isAlive()) {
                clientDataReceiver.interrupt();
                clientDataReceiver.join(100);
            }

            socket.close();
        } catch (InterruptedException e) {
        } catch (IOException e) {
        } finally {
            clientDataReceiver = null;
            sendData = null;
            socket = null;
            status = CONNECTION_STATUS.DISCONNECTED;
            playlist.clear();
            mediaListPlayer.stop();
        }
    }

    /**
     * Release all resources.
     */
    public void release() {
        closeConnection();

        playlist.clear();
        mediaListPlayer.stop();
        playlist.release();
        mediaListPlayer.release();
        factory.release();
    }

    /**
     * Enum for the status of the connection.
     */
    public enum CONNECTION_STATUS {
        CONNECTED,
        DISCONNECTED
    }

    /**
     * Enum for differents possible requests to the client.
     */
    public enum REQUEST_CLIENT {
        STREAMING_STARTED,
        DISCONNECTION
    }
}
