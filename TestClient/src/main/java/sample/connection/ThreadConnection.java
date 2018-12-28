package sample.connection;

import static sample.connection.socket.StopReason.STOP_DUE_TO_NOMINAL_EXIT;

/**
 * Created by thomasfouan on 22/04/2016.
 */
public class ThreadConnection {

    private final ConnectionHandler connectionHandler;
    private final Thread thread;

    public ThreadConnection(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.thread = new Thread(connectionHandler::computeConnections);
    }

    public void start() {
        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread.isAlive()) {
            connectionHandler.stopServer(STOP_DUE_TO_NOMINAL_EXIT);
            thread.interrupt();
        }
        thread.join();
    }
}
