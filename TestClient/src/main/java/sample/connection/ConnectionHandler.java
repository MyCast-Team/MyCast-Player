package sample.connection;

import sample.connection.socket.ServerSocketService.StopReason;
import sample.connection.socket.SocketService;
import sample.output.OutputPrinterService;
import sample.player.ResizablePlayer;
import uk.co.caprica.vlcj.mrl.RtspMrl;

import java.io.IOException;

import static sample.connection.ClientRequest.DISCONNECTION;
import static sample.connection.ClientRequest.STREAMING_STARTED;
import static sample.connection.socket.ServerSocketService.StopReason.STOP_DUE_TO_EXCEPTION;

public class ConnectionHandler {

    private static final int STREAMING_PORT = 5555;

    private final ResizablePlayer resizablePlayer;
    private final SocketService socketService;
    private final OutputPrinterService outputPrinterService;

    public ConnectionHandler(ResizablePlayer resizablePlayer, SocketService socketService, OutputPrinterService outputPrinterService) {
        this.resizablePlayer = resizablePlayer;
        this.socketService = socketService;
        this.outputPrinterService = outputPrinterService;
    }

    void computeConnections() {
        print("Server is listening on port : " + socketService.getServerLocalPort());

        while (!socketService.isClosed()) {
            try {
                print("Waiting for another connection");
                socketService.waitForNewConnection();
                print("Connected with " + socketService.getClientHostName());

                // While there is no request for disconnection, waiting for the start of the streaming from the client
                ClientRequest command;
                while ((command = socketService.getNextClientRequest()) != DISCONNECTION) {
                    if (command == STREAMING_STARTED) {
                        // Start receiving data from client application and play it
                        startPlayingMediaFromStreaming();
                    }
                }

                // Disconnection requested, close the socket, and wait for another connection...
                resizablePlayer.stop();
                socketService.tryCloseSocket();
            } catch (IOException e) {
                stopServer(STOP_DUE_TO_EXCEPTION);
            } catch (NullPointerException e) {
                print("The current socket threw a NullPointerException...");
                endClientConnection();
            } catch (StopServerException ignored) {
            }
        }
    }

    void stopServer(StopReason stopReason) {
        socketService.tryClose(stopReason);

        endClientConnection();
    }

    private void print(String s) {
        outputPrinterService.print(s);
    }

    private void startPlayingMediaFromStreaming() {
        String mrl = new RtspMrl()
                .host(socketService.getClientHostAddress())
                .port(STREAMING_PORT)
                .path("/demo")
                .value();

        resizablePlayer.playMedia(mrl);
    }

    private void endClientConnection() {
        resizablePlayer.stop();

        outputPrinterService.print("Send disconnection request to server");
        socketService.sendExitRequest();
    }
}
