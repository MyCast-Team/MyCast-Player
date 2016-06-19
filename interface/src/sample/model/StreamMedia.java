package sample.model;

import javafx.scene.control.Alert;
import javafx.util.Pair;
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
public class StreamMedia {

    private final MediaPlayerFactory factory;

    private final MediaListPlayer mediaListPlayer;

    private final MediaList playList;

    private final int PORT = 2016;

    private Socket socket;

    private CONNECTION_STATUS status = CONNECTION_STATUS.DISCONNECTED;

    private PrintWriter sendData;

    /**
     * CONSTRUCTOR
     */
    public StreamMedia() {
        socket = null;
        factory = new MediaPlayerFactory();
        mediaListPlayer = factory.newMediaListPlayer();
        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
            @Override
            public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
                System.out.println("Playing next item: " + itemMrl + " (" + item + ")");
            }
        });
        playList = factory.newMediaList();
    }

    /**
     * GETTERS
     */
    public CONNECTION_STATUS getStatus() { return status; }

    public Socket getSocket() { return socket; }

    public MediaPlayerFactory getFactory() { return factory; }

    public MediaListPlayer getMediaListPlayer() { return mediaListPlayer; }

    public MediaList getPlayList() { return playList; }

    /**
     * Prepare the player for streaming.
     * @param addr address of the client
     */
    public void prepareStreamingMedia(String addr) {

        String rtspStream = formatRtspStream(addr, PORT, "demo");
        System.out.println("Prepare for streaming at : "+rtspStream);
        playList.setStandardMediaOptions(rtspStream,
                ":no-sout-rtp-sap",
                ":no-sout-standard-sap",
                ":sout-all",
                ":sout-keep");
        mediaListPlayer.setMediaList(playList);
    }

    /**
     * Start the streaming at the address and port set with the prepareStreamingMedia function.
     */
    public void startStreamingMedia() {
        mediaListPlayer.play();
        // Wait few milliseconds to make sure the MediaListPlayer is ready for the stream
        // before client starts receiving data
        try {
            Thread.sleep(100);
            System.out.println("Play track 1 of "+mediaListPlayer.getMediaList().size());
            sendData.println(StreamMedia.REQUEST_CLIENT.STREAMING_STARTED.ordinal());
            sendData.flush();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pauseStreamingMedia() {
        mediaListPlayer.pause();
    }

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

    public boolean setClientConnection() {
        status = CONNECTION_STATUS.DISCONNECTED;

        ConnectionDialog connectionDialog = new ConnectionDialog();
        Optional<Pair<String, Integer>> result = connectionDialog.getDialog().showAndWait();
        connectionDialog.getDialog().close();
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

    public void closeConnection() {

        try {
            sendData.println(StreamMedia.REQUEST_CLIENT.DISCONNECTION.ordinal());
            sendData.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        status = CONNECTION_STATUS.DISCONNECTED;
        mediaListPlayer.stop();
        playList.clear();
    }

    public enum CONNECTION_STATUS {
        CONNECTED,
        DISCONNECTED
    }

    public enum REQUEST_CLIENT {
        STREAMING_STARTED,
        DISCONNECTION
    }
}
