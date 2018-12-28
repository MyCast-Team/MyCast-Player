package sample.connection.socket;

import sample.connection.ClientRequest;
import sample.connection.StopServerException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static sample.connection.socket.StopReason.STOP_DUE_TO_EXCEPTION;
import static sample.connection.socket.StopReason.STOP_DUE_TO_NOMINAL_EXIT;

public class ServerSocketService implements SocketService {

    private final ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private StopReason stopReason;

    public ServerSocketService(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        stopReason = STOP_DUE_TO_EXCEPTION;
    }

    @Override
    public int getServerLocalPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void tryClose(StopReason stopReason) {
        this.stopReason = stopReason;
        try {
            // This will throw an IOException to end the accept() method
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean isClosed() {
        return serverSocket.isClosed();
    }

    @Override
    public void waitForNewConnection() throws IOException, StopServerException {
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            if (stopReason == STOP_DUE_TO_NOMINAL_EXIT) {
                throw new StopServerException();
            }
            throw e;
        }
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public String getClientHostName() {
        return socket.getInetAddress().getCanonicalHostName();
    }

    @Override
    public void tryCloseSocket() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void sendExitRequest() {
        if (isSocketOpened()) {
            writeExitRequestInSocket();

            tryCloseSocket();
        }
    }

    @Override
    public ClientRequest getNextClientRequest() throws IOException {
        String data = bufferedReader.readLine();
        return ClientRequest.getValueFromData(data);
    }

    @Override
    public String getClientHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    private boolean isSocketOpened() {
        return socket != null && !socket.isClosed();
    }

    private void writeExitRequestInSocket() {
        printWriter.println(ClientRequest.DISCONNECTION.ordinal());
        printWriter.flush();
    }
}
