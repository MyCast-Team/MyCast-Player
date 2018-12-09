package sample.model;

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
