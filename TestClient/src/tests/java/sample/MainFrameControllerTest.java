package sample;

import org.junit.Test;
import sample.connection.ThreadConnection;
import sample.player.ResizablePlayer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MainFrameControllerTest {

    @Test
    public void should_start_thread_connection_in_initialisation() {
        ResizablePlayer player = mock(ResizablePlayer.class);
        ThreadConnection threadConnection = mock(ThreadConnection.class);

        new MainFrameController(player, threadConnection);

        verify(threadConnection).start();
    }

    @Test
    public void should_stop_thread_connection_and_release_player_when_release_resources() throws InterruptedException {
        ResizablePlayer player = mock(ResizablePlayer.class);
        ThreadConnection threadConnection = mock(ThreadConnection.class);

        MainFrameController mainFrameController = new MainFrameController(player, threadConnection);

        mainFrameController.release();

        verify(threadConnection).stop();
        verify(player).release();
    }
}
