package sample.connection;

import org.junit.Test;

import static org.mockito.Mockito.*;
import static sample.connection.socket.StopReason.STOP_DUE_TO_NOMINAL_EXIT;

public class ThreadConnectionTest {

    @Test
    public void should_not_stop_server_when_stop_dead_thread() throws InterruptedException {
        ConnectionHandler connectionHandlerMock = mock(ConnectionHandler.class);
        ThreadConnection threadConnection = new ThreadConnection(connectionHandlerMock);

        threadConnection.stop();

        verify(connectionHandlerMock, never()).stopServer(STOP_DUE_TO_NOMINAL_EXIT);
    }

    @Test
    public void should_stop_server_when_stop_alive_thread() throws InterruptedException {
        ConnectionHandler connectionHandlerMock = mock(ConnectionHandler.class);
        ThreadConnection threadConnection = new ThreadConnection(connectionHandlerMock);

        threadConnection.start();
        threadConnection.stop();

        verify(connectionHandlerMock).stopServer(STOP_DUE_TO_NOMINAL_EXIT);
    }
}