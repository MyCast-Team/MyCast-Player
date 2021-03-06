package sample.model;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import sample.annotation.DocumentationAnnotation;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

/**
 * Created by thomasfouan on 24/04/2016.
 *
 * Get a BufferFormat accordingly to the dimensions of the screen.
 * Update the videoSourceRatioProperty variable with the dimensions of the source (media).
 */
@DocumentationAnnotation(author = "Thomas Fouan", date="24/04/2016", description = "This class is used in the ResizablePlayer class. It's a buffer where the image is written to. Its size depends on the player size.")
public class CanvasBufferFormatCallback implements BufferFormatCallback {

    FloatProperty videoSourceRatioProperty;

    public CanvasBufferFormatCallback(FloatProperty videoSourceRatioProperty) {
        super();
        this.videoSourceRatioProperty = videoSourceRatioProperty;
    }

    /**
     * Get a buffer accordingly to the player size.
     * @param sourceWidth
     * @param sourceHeight
     * @return BufferFormat
     */
    @Override
    public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        Platform.runLater(() -> videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth));
        return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
    }
}
