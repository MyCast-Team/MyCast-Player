package sample.player;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

/**
 * Created by thomasfouan on 24/04/2016.
 *
 * Get a BufferFormat accordingly to the dimensions of the screen.
 * Update the videoSourceRatioProperty variable with the dimensions of the source (media).
 */
class CanvasBufferFormatCallback implements BufferFormatCallback {

    private FloatProperty videoSourceRatioProperty;

    CanvasBufferFormatCallback(FloatProperty videoSourceRatioProperty) {
        super();
        this.videoSourceRatioProperty = videoSourceRatioProperty;
    }

    @Override
    public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        Platform.runLater(() -> videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth));
        return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
    }
}
