package com.test;

import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.mrl.RtspMrl;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by thomasfouan on 22/04/2016.
 */
public class ThreadConnection extends Thread {

    private ServerSocket serverSocket;

    private Socket socket;

    //private MediaPlayerFactory mFactory;

    //private HeadlessMediaPlayer mPlayer;

    private DirectMediaPlayer mediaPlayer;

    private String mrl;

    private final int PORT = 12345;

    private final int STREAMING_PORT = 2016;

    private BufferedReader bufferedReader;

    private String receivedData;

    public ThreadConnection(DirectMediaPlayer mediaPlayer) throws IOException {
        //mFactory = new MediaPlayerFactory();
        //mPlayer = mFactory.newHeadlessMediaPlayer();

        this.mediaPlayer = mediaPlayer;

        this.serverSocket = new ServerSocket(PORT);
        System.out.println("Server is listening on port : "+serverSocket.getLocalPort());
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            // This will throw an IOException to end the accept() method
            serverSocket.close();
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!serverSocket.isClosed()) {
            try {
                boolean isStreamingStarted = false;
                System.out.println("Start of the loop");
                socket = serverSocket.accept();
                System.out.println("Connection has been accepted !");

                mrl = new RtspMrl().host(socket.getInetAddress().getHostAddress()).port(STREAMING_PORT).path("/demo").value();
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //While there is no request for deconnection, waiting for the start of the streaming from the client
                while(!(receivedData = bufferedReader.readLine()).equals("DECONNECTION")) {
                    System.out.println(receivedData);
                    if(!isStreamingStarted && receivedData.equals("STREAMING_STARTED")) {
                        //Start receiving data from client application and play it
                        mediaPlayer.prepareMedia(mrl);
                        mediaPlayer.playMedia(mrl);
                    } else if(receivedData.equals("STREAMING_STOPPED")) {
                        //mPlayer.stop();
                    }
                }

                //Demande de deconnexion, on revient au debut, et on ferme ce socket
                mediaPlayer.stop();
                socket.close();
            } catch (InterruptedIOException e) {
                System.out.println("The ThreadConnection has been interrupted throw InterruptedIOException...");
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                if (!isInterrupted()) {
                    System.out.println("An IOException has been thrown...");
                    e.printStackTrace();
                } else {
                    System.out.println("The ThreadConnection has been interrupted throw IOException...");
                    Thread.currentThread().interrupt();
                }
            } catch (NullPointerException e) {
                System.out.println("The current socket throwed a NullPointerException...");
            } finally {
                if(!isInterrupted()) {
                    System.out.println("End of connection. Waiting for another connection...");
                }
            }
        }

        System.out.println("End of the ThreadConnection...");
    }

    private enum REQUEST_CLIENT {
        STREAMING_STARTED,
        STREAMING_STOPPED,
        DECONNECTION
    }
}