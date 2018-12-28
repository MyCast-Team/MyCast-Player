package sample.connection;

import org.junit.Before;
import org.junit.Test;
import sample.connection.socket.SocketService;
import sample.connection.socket.StopReason;
import sample.output.OutputPrinterService;
import sample.player.ResizablePlayer;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static sample.connection.socket.StopReason.STOP_DUE_TO_EXCEPTION;

public class ConnectionHandlerTest {

    private ResizablePlayer resizablePlayerMock;
    private SocketService socketServiceMock;
    private OutputPrinterService outputPrinterServiceMock;
    private ConnectionHandler connectionHandler;

    @Before
    public void setUp() {
        resizablePlayerMock = mock(ResizablePlayer.class);
        socketServiceMock = mock(SocketService.class);
        outputPrinterServiceMock = mock(OutputPrinterService.class);

        connectionHandler = new ConnectionHandler(resizablePlayerMock, socketServiceMock, outputPrinterServiceMock);
    }

    @Test
    public void should_not_accept_connections_when_socket_is_closed() throws IOException, StopServerException {
        int port = 1000;
        when(socketServiceMock.getServerLocalPort()).thenReturn(port);
        when(socketServiceMock.isClosed()).thenReturn(true);

        connectionHandler.computeConnections();

        verify(socketServiceMock, never()).waitForNewConnection();
        verify(outputPrinterServiceMock).print("Server is listening on port : " + port);
    }

    @Test
    public void should_accept_a_connection_then_close_it_when_socket_is_not_closed_then_closed() throws IOException, StopServerException {
        int port = 1000;
        when(socketServiceMock.getServerLocalPort()).thenReturn(port);
        when(socketServiceMock.isClosed()).thenReturn(false, true);
        when(socketServiceMock.getClientHostName()).thenReturn("localhost");
        when(socketServiceMock.getNextClientRequest()).thenReturn(ClientRequest.DISCONNECTION);

        connectionHandler.computeConnections();

        verify(socketServiceMock, times(2)).isClosed();
        verify(socketServiceMock).waitForNewConnection();
        verify(socketServiceMock).getClientHostName();
        verify(socketServiceMock, never()).getClientHostAddress();
        verify(socketServiceMock).getNextClientRequest();
        verify(socketServiceMock).tryCloseSocket();
        verify(outputPrinterServiceMock).print("Server is listening on port : " + port);
        verify(outputPrinterServiceMock).print("Connected with localhost");
        verify(resizablePlayerMock).stop();
        verify(resizablePlayerMock, never()).playMedia(anyString());
    }

    @Test
    public void should_stop_connection_when_receiving_disconnection_command() throws IOException, StopServerException {
        int port = 1000;
        when(socketServiceMock.getServerLocalPort()).thenReturn(port);
        when(socketServiceMock.isClosed()).thenReturn(false, true);
        when(socketServiceMock.getClientHostName()).thenReturn("localhost");
        when(socketServiceMock.getNextClientRequest()).thenReturn(ClientRequest.DISCONNECTION);

        connectionHandler.computeConnections();

        verify(socketServiceMock, times(2)).isClosed();
        verify(socketServiceMock).waitForNewConnection();
        verify(socketServiceMock).getClientHostName();
        verify(socketServiceMock, never()).getClientHostAddress();
        verify(socketServiceMock).getNextClientRequest();
        verify(socketServiceMock).tryCloseSocket();
        verify(outputPrinterServiceMock).print("Server is listening on port : " + port);
        verify(outputPrinterServiceMock).print("Connected with localhost");
        verify(resizablePlayerMock).stop();
        verify(resizablePlayerMock, never()).playMedia(anyString());
    }

    @Test
    public void should_play_media_when_receiving_streaming_started_command() throws IOException, StopServerException {
        int port = 1000;
        when(socketServiceMock.getServerLocalPort()).thenReturn(port);
        when(socketServiceMock.isClosed()).thenReturn(false, true);
        when(socketServiceMock.getClientHostName()).thenReturn("localhost");
        when(socketServiceMock.getNextClientRequest()).thenReturn(
                ClientRequest.STREAMING_STARTED,
                ClientRequest.DISCONNECTION
        );

        connectionHandler.computeConnections();

        verify(socketServiceMock, times(2)).isClosed();
        verify(socketServiceMock).waitForNewConnection();
        verify(socketServiceMock).getClientHostName();
        verify(socketServiceMock).getClientHostAddress();
        verify(socketServiceMock, times(2)).getNextClientRequest();
        verify(socketServiceMock).tryCloseSocket();
        verify(outputPrinterServiceMock).print("Server is listening on port : " + port);
        verify(outputPrinterServiceMock).print("Connected with localhost");
        verify(resizablePlayerMock).playMedia(anyString());
        verify(resizablePlayerMock).stop();
    }

    @Test
    public void should_stop_server_and_send_disconnection_request_to_client_when_IOException_is_thrown_while_waiting_for_new_connection() throws IOException, StopServerException {
        int port = 1000;
        when(socketServiceMock.getServerLocalPort()).thenReturn(port);
        when(socketServiceMock.isClosed()).thenReturn(false, true);
        doThrow(new IOException()).when(socketServiceMock).waitForNewConnection();
        when(socketServiceMock.getClientHostName()).thenReturn("localhost");
        when(socketServiceMock.getNextClientRequest()).thenThrow(new IOException());

        connectionHandler.computeConnections();

        verify(socketServiceMock, times(2)).isClosed();
        verify(socketServiceMock).waitForNewConnection();
        verify(socketServiceMock, never()).getClientHostName();
        verify(socketServiceMock, never()).getClientHostAddress();
        verify(socketServiceMock, never()).getNextClientRequest();
        verify(socketServiceMock).tryClose(STOP_DUE_TO_EXCEPTION);
        verify(socketServiceMock).sendExitRequest();
        verify(socketServiceMock, never()).tryCloseSocket();
        verify(outputPrinterServiceMock).print("Server is listening on port : " + port);
        verify(outputPrinterServiceMock, never()).print("Connected with localhost");
        verify(outputPrinterServiceMock).print("Send disconnection request to server");
        verify(resizablePlayerMock, never()).playMedia(anyString());
        verify(resizablePlayerMock).stop();
    }

    @Test
    public void should_stop_server_and_send_disconnection_request_to_client_when_IOException_is_thrown_while_reading_next_client_request() throws IOException, StopServerException {
        int port = 1000;
        when(socketServiceMock.getServerLocalPort()).thenReturn(port);
        when(socketServiceMock.isClosed()).thenReturn(false, true);
        when(socketServiceMock.getClientHostName()).thenReturn("localhost");
        when(socketServiceMock.getNextClientRequest()).thenThrow(new IOException());

        connectionHandler.computeConnections();

        verify(socketServiceMock, times(2)).isClosed();
        verify(socketServiceMock).waitForNewConnection();
        verify(socketServiceMock).getClientHostName();
        verify(socketServiceMock, never()).getClientHostAddress();
        verify(socketServiceMock).getNextClientRequest();
        verify(socketServiceMock).tryClose(STOP_DUE_TO_EXCEPTION);
        verify(socketServiceMock).sendExitRequest();
        verify(socketServiceMock, never()).tryCloseSocket();
        verify(outputPrinterServiceMock).print("Server is listening on port : " + port);
        verify(outputPrinterServiceMock).print("Connected with localhost");
        verify(outputPrinterServiceMock).print("Send disconnection request to server");
        verify(resizablePlayerMock, never()).playMedia(anyString());
        verify(resizablePlayerMock).stop();
    }

    @Test
    public void should_send_disconnection_request_to_client_when_NullPointerException_is_thrown() throws IOException, StopServerException {
        int port = 1000;
        when(socketServiceMock.getServerLocalPort()).thenReturn(port);
        when(socketServiceMock.isClosed()).thenReturn(false, true);
        when(socketServiceMock.getClientHostName()).thenReturn("localhost");
        when(socketServiceMock.getNextClientRequest()).thenThrow(new NullPointerException());

        connectionHandler.computeConnections();

        verify(socketServiceMock, times(2)).isClosed();
        verify(socketServiceMock).waitForNewConnection();
        verify(socketServiceMock).getClientHostName();
        verify(socketServiceMock, never()).getClientHostAddress();
        verify(socketServiceMock).getNextClientRequest();
        verify(socketServiceMock, never()).tryClose(any(StopReason.class));
        verify(socketServiceMock).sendExitRequest();
        verify(socketServiceMock, never()).tryCloseSocket();
        verify(outputPrinterServiceMock).print("Server is listening on port : " + port);
        verify(outputPrinterServiceMock).print("Connected with localhost");
        verify(outputPrinterServiceMock).print("The current socket threw a NullPointerException...");
        verify(outputPrinterServiceMock).print("Send disconnection request to server");
        verify(resizablePlayerMock, never()).playMedia(anyString());
        verify(resizablePlayerMock).stop();
    }
}