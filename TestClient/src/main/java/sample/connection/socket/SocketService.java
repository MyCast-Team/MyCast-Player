package sample.connection.socket;

import sample.connection.ClientRequest;
import sample.connection.StopServerException;

import java.io.IOException;

public interface SocketService {

    int getServerLocalPort();

    void tryClose(ServerSocketService.StopReason stopReason);

    boolean isClosed();

    void waitForNewConnection() throws IOException, StopServerException;

    String getClientHostAddress();

    String getClientHostName();

    ClientRequest getNextClientRequest() throws IOException;

    void tryCloseSocket();

    void sendExitRequest();
}
