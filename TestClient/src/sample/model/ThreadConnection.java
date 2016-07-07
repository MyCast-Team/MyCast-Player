package sample.model;

import uk.co.caprica.vlcj.mrl.RtspMrl;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;

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

    private DirectMediaPlayer mediaPlayer;

    private String mrl;

    private final int PORT = 12345;

    private final int STREAMING_PORT = 2016;

    private BufferedReader bufferedReader;

    public ThreadConnection(DirectMediaPlayer mediaPlayer) throws IOException {

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
        int receivedData;
        while(!serverSocket.isClosed()) {
            try {
                socket = serverSocket.accept();

                mrl = new RtspMrl().host(socket.getInetAddress().getHostAddress()).port(STREAMING_PORT).path("/demo").value();
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //While there is no request for disconnection, waiting for the start of the streaming from the client
                while((receivedData = Integer.parseInt(bufferedReader.readLine())) != REQUEST_CLIENT.DISCONNECTION.ordinal()) {
                    if(receivedData == REQUEST_CLIENT.STREAMING_STARTED.ordinal()) {
                        //Start receiving data from client application and play it
                        mediaPlayer.prepareMedia(mrl);
                        mediaPlayer.playMedia(mrl);
<<<<<<< HEAD
=======
                        isStreamingStarted = true;
>>>>>>> parent of 0c6cf26... Minor bug fixed
                    }
                }

                //Disconnection requested, close the socket, and wait for another connection...
                mediaPlayer.stop();
                socket.close();
            } catch (InterruptedIOException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            } catch (NullPointerException e) {
                System.out.println("The current socket thrown a NullPointerException...");
            }
        }
    }

    private enum REQUEST_CLIENT {
        STREAMING_STARTED,
        DISCONNECTION
    }
}
