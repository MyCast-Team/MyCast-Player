package sample.model;

import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.scene.image.PixelWriter;
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
public class CanvasPlayerComponent extends DirectMediaPlayerComponent {

    private PixelWriter pixelWriter = null;

    private WritableImage writableImage;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    public CanvasPlayerComponent(WritableImage writableImage, WritablePixelFormat<ByteBuffer> pixelFormat, FloatProperty videoSourceRatioProperty) {
        super(new CanvasBufferFormatCallback(videoSourceRatioProperty));

        this.writableImage = writableImage;
        this.pixelFormat = pixelFormat;
    }

    private PixelWriter getPW() {
        if (pixelWriter == null) {
            pixelWriter = writableImage.getPixelWriter();
        }
        return pixelWriter;
    }

    @Override
    public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
        if (writableImage == null) {
            return;
        }
        Platform.runLater(() -> {
            try {
                Memory[] memories = mediaPlayer.lock();
                if(memories != null) {
                    Memory nativeBuffer = memories[0];
                    ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                    getPW().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
            finally {
                mediaPlayer.unlock();
            }
        });
    }
}
