package sample.model;

import javafx.scene.control.Alert;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

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

    private static final String PATH_TO_VIDEO = "/Users/thomasfouan/Desktop/video.avi";

    /**
     * CONSTRUCTOR
     */
    public StreamMedia() {
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
     * @param port port to use for the streaming
     */
    public void prepareStreamingMedia(String addr, int port) {

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
            Thread.currentThread().sleep(100);
            System.out.println("Play track 1 of "+mediaListPlayer.getMediaList().size());
            sendData.println(StreamMedia.REQUEST_CLIENT.STREAMING_STARTED);
            sendData.flush();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pauseStreamingMedia() {

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

    public boolean setClientConnection(String addr, int port) {

        socket = null;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connection information");
        alert.setHeaderText(null);
        alert.setContentText("The connection to the client has been successfully done !");

        try {
            if(port < 0) {
                alert.setContentText("Invalid port number !");
            } else {
                socket = new Socket(addr, port);
            }
        } catch (ConnectException e) {
            alert.setContentText("The connection to the client has been failed ! Make sure the client is already started !");
        } catch (IOException e) {
            alert.setContentText("An error occurred during making the connection to the client ! Please try later !");
        } finally {
            alert.showAndWait();

            if(socket == null) {
                status = CONNECTION_STATUS.DISCONNECTED;
                return false;
            } else {
                try {
                    sendData = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
                    status = CONNECTION_STATUS.CONNECTED;
                    prepareStreamingMedia(socket.getInetAddress().getHostAddress(), socket.getLocalPort());
                    return true;
                } catch (IOException e) {
                    closeConnection();
                    return false;
                }
            }
        }
    }

    public void closeConnection() {

        if(sendData != null) {
            sendData.println(StreamMedia.REQUEST_CLIENT.DECONNECTION);
            sendData.flush();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        status = CONNECTION_STATUS.DISCONNECTED;
    }

    public static enum CONNECTION_STATUS {
        CONNECTED,
        DISCONNECTED
    }

    public static enum REQUEST_CLIENT {
        STREAMING_STARTED,
        STREAMING_STOPPED,
        DECONNECTION
    }
}
