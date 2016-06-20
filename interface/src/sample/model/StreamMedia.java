package sample.model;

import javafx.scene.control.Alert;
import javafx.util.Pair;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
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
public class StreamMedia {

    private MediaPlayerFactory factory;
    private MediaListPlayer mediaListPlayer;
    private MediaList playList;
    private Playlist interfacePlaylist;

    private Socket socket;
    private PrintWriter sendData;

    private boolean isAlreadyStarted;

    private CONNECTION_STATUS status = CONNECTION_STATUS.DISCONNECTED;

    private final int PORT = 2016;

    /**
     * CONSTRUCTOR
     */
    public StreamMedia() {
        factory = null;
        mediaListPlayer = null;
        playList = null;
        interfacePlaylist = null;
        socket = null;
        sendData = null;
    }

    /**
     * GETTERS
     */
    public CONNECTION_STATUS getStatus() { return status; }

    public Socket getSocket() { return socket; }

    public MediaListPlayer getMediaListPlayer() { return mediaListPlayer; }

    public MediaList getPlayList() { return playList; }

    /**
     * SETTERS
     */
    public void setInterfacePlaylist(Playlist interfacePlaylist) {
        this.interfacePlaylist = interfacePlaylist;
    }


    /**
     * Create and prepare a player for streaming.
     * @param addr address of the client
     */
    public void prepareStreamingMedia(String addr) {

        factory = new MediaPlayerFactory();
        mediaListPlayer = factory.newMediaListPlayer();
        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
            @Override
            public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
                System.out.println("Playing next item: " + itemMrl + " (" + item + ")");
            }
        });
        playList = factory.newMediaList();

        String rtspStream = formatRtspStream(addr, PORT, "demo");
        System.out.println("Prepare for streaming at : "+rtspStream);
        playList.setStandardMediaOptions(rtspStream,
                ":no-sout-rtp-sap",
                ":no-sout-standard-sap",
                ":sout-all",
                ":sout-keep");
        mediaListPlayer.setMediaList(playList);
        addMediaInStreamingPlaylist();
        isAlreadyStarted = false;
    }

    /**
     * Return a string representing a RTSP stream URL for the given address, port and id.
     * @param serverAddress
     * @param serverPort
     * @param id
     * @return String
     */
    private String formatRtspStream(String serverAddress, int serverPort, String id) {
        StringBuilder sb = new StringBuilder(60);
        sb.append(":sout=#rtp{sdp=rtsp://@");
        sb.append(serverAddress);
        sb.append(':');
        sb.append(serverPort);
        sb.append('/');
        sb.append(id);
        sb.append("}");
        return sb.toString();
    }

    private void addMediaInStreamingPlaylist() {
        for (Media media : interfacePlaylist.getPlaylist()) {
            playList.addMedia(media.getPath());
        }
    }

    /**
     * Start the streaming at the address and port set with the prepareStreamingMedia function.
     */
    public void startStreamingMedia() {
        try {
            mediaListPlayer.play();
            // Wait few milliseconds to make sure the MediaListPlayer is ready for the stream
            // before client starts receiving data

            if (!isAlreadyStarted) {
                Thread.sleep(100);
                sendData.println(StreamMedia.REQUEST_CLIENT.STREAMING_STARTED.ordinal());
                sendData.flush();
                isAlreadyStarted = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
        status = CONNECTION_STATUS.DISCONNECTED;

        ConnectionDialog connectionDialog = new ConnectionDialog();
        Optional<Pair<String, Integer>> result = connectionDialog.getDialog().showAndWait();

        if (result.isPresent()) {
            String addr = result.get().getKey();
            int port = result.get().getValue();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connection information");
            alert.setHeaderText(null);

            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(addr, port), 1000);

                sendData = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
                prepareStreamingMedia(socket.getInetAddress().getHostAddress());

                status = CONNECTION_STATUS.CONNECTED;
                alert.setContentText("The connection with '" + socket.getInetAddress().getCanonicalHostName() + "' has been successfully done !");
            } catch (UnknownHostException e) {
                alert.setContentText("Invalid Address !");
            } catch (IllegalArgumentException e) {
                alert.setContentText("Invalid port number !");
            } catch (SocketTimeoutException | ConnectException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                }
                alert.setContentText("The connection to the client has been failed ! Make sure the client is already started !");
            } catch (IOException e) {
                alert.setContentText("An error occurred during making the connection to the client ! Please try later !");
            } finally {
                alert.showAndWait();
            }
        }

        return status.equals(CONNECTION_STATUS.CONNECTED);
    }

    /**
     * Close the connection with the current client. Release all resources attached to this connection.
     */
    public void closeConnection() {

        try {
            sendData.println(StreamMedia.REQUEST_CLIENT.DISCONNECTION.ordinal());
            sendData.flush();
            sendData.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sendData = null;
            socket = null;
            status = CONNECTION_STATUS.DISCONNECTED;
            playList.clear();
            playList.release();
            mediaListPlayer.stop();
            mediaListPlayer.release();
            factory.release();
        }
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
