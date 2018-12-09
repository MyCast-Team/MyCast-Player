package sample.player;

import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;

import java.nio.ByteBuffer;

/**
 * Created by thomasfouan on 24/04/2016.
 *
 * Control the writing of images in the player : update the writableImage which is link to the player pane.
 */
class CanvasPlayerComponent extends DirectMediaPlayerComponent {

    private WritableImage writableImage;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    CanvasPlayerComponent(WritableImage writableImage, WritablePixelFormat<ByteBuffer> pixelFormat, CanvasBufferFormatCallback bufferFormatCallback) {
        super(bufferFormatCallback);

        this.writableImage = writableImage;
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
        if (writableImage == null) {
            return;
        }
        Platform.runLater(() -> {
            try {
                Memory[] memories = mediaPlayer.lock();
                if (memories != null) {
                    Memory nativeBuffer = memories[0];
                    ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                    writableImage
                            .getPixelWriter()
                            .setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
            } finally {
                mediaPlayer.unlock();
            }
        });
    }
}
