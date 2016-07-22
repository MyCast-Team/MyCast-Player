package sample.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sample.constant.Constant;
import uk.co.caprica.vlcj.mrl.RtspMrl;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by thomasfouan on 22/04/2016.
 */
public class ThreadConnection extends Thread {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    private MediaPlayer mediaPlayer;
    private Pane playerHolder;
    private ImageView imageView;
    private ImageView artworkView;

    private String mrl;

    public ThreadConnection(MediaPlayer mediaPlayer, Pane playerHolder, ImageView imageView, ImageView artworkView) throws IOException {

        this.mediaPlayer = mediaPlayer;
        this.playerHolder = playerHolder;
        this.imageView = imageView;
        this.artworkView = artworkView;

        this.serverSocket = new ServerSocket(Constant.PORT);
        System.out.println("Server is listening on port : "+serverSocket.getLocalPort());
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            mediaPlayer.stop();
            // This will throw an IOException to end the accept() method
            serverSocket.close();
            if(socket != null && !socket.isClosed()) {
                System.out.println("Send disconnection request to server");
                printWriter.println(REQUEST_CLIENT.DISCONNECTION.ordinal());
                printWriter.flush();
                socket.close();
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        String data;
        int receivedData;

        while(!serverSocket.isClosed()) {
            try {
                System.out.println("Waiting for another connection");
                socket = serverSocket.accept();
                System.out.println("Connected with "+socket.getInetAddress().getCanonicalHostName());

                mrl = new RtspMrl().host(socket.getInetAddress()
                                    .getHostAddress())
                                    .port(Constant.STREAMING_PORT)
                                    .path("/demo").value();

                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                //While there is no request for disconnection, waiting for the start of the streaming from the client
                while((data = bufferedReader.readLine()) != null && !data.trim().equals("")) {
                    receivedData = Integer.parseInt(data);
                    if(receivedData == REQUEST_CLIENT.STREAMING_STARTED.ordinal()) {
                        //Start receiving data from client application and play it
                        mediaPlayer.playMedia(mrl);
                        //computeImageView();
                    } else if(receivedData == REQUEST_CLIENT.DISCONNECTION.ordinal()) {
                        break;
                    }
                }
            } catch (InterruptedIOException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            } catch (NullPointerException e) {
                System.out.println("The current socket thrown a NullPointerException...");
            } finally {
                //Disconnection requested, close the socket, and wait for another connection...
                mediaPlayer.stop();
                try {
                    if(socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    private void computeImageView() {
        boolean isMusic = false;
        MediaMeta metaInfo = mediaPlayer.getMediaMeta();
        String artworkUrl = metaInfo.getArtworkUrl();

        System.out.println(mediaPlayer.getMediaDetails());
        System.out.println(mediaPlayer.getMediaMetaData());
        System.out.println(mediaPlayer.getMediaType());
        System.out.println(mediaPlayer.userData());
        System.out.println(metaInfo);
        System.out.println(metaInfo.getUrl());
        System.out.println(artworkUrl);
        System.out.println(metaInfo.getArtist());

        String url = metaInfo.getUrl();
        System.out.println(metaInfo.getEncodedBy());

        for(String ext : Constant.EXTENSIONS_AUDIO) {
            if(ext.equals(url.substring(url.lastIndexOf(".")+1))) {
                isMusic = true;
                break;
            }
        }

        if(isMusic) {
            if (artworkUrl != null) {
                artworkView.setImage(new Image(artworkUrl));
                artworkView.setX(playerHolder.getWidth()/2 - artworkView.getImage().getWidth()/2);
                artworkView.setY(playerHolder.getHeight()/2 - artworkView.getImage().getHeight()/2);
            }
            imageView.setVisible(false);
            artworkView.setVisible(true);
        } else {
            artworkView.setVisible(false);
            imageView.setVisible(true);
        }
    }

    private enum REQUEST_CLIENT {
        STREAMING_STARTED,
        DISCONNECTION
    }
}
