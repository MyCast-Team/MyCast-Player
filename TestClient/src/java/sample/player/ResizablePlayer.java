package sample.player;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * Class of creation and control of the vlcj player.
 */
public class ResizablePlayer {

    private DirectMediaPlayerComponent mediaPlayerComponent;
    private MediaPlayer mediaPlayer;

    private ImageView imageView;
    private WritableImage writableImage;

    private Pane playerHolder;
    private FloatProperty videoSourceRatioProperty;

    public ResizablePlayer(Pane playerHolder, ImageView imageView) {

        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);

        this.playerHolder = playerHolder;
        this.imageView = imageView;

        initializeImageView();

        CanvasBufferFormatCallback bufferFormatCallback = new CanvasBufferFormatCallback(videoSourceRatioProperty);
        mediaPlayerComponent = new CanvasPlayerComponent(writableImage, PixelFormat.getByteBgraPreInstance(), bufferFormatCallback);
        mediaPlayer = mediaPlayerComponent.getMediaPlayer();
    }

    public void playMedia(String mrl) {
        mediaPlayer.playMedia(mrl);
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void release() {
        this.mediaPlayerComponent.release(true);
        this.mediaPlayer.release();
    }

    /**
     * initialize the type of image (size, ratio) to write in the player, accordingly with :
     *      - the dimensions of the screen
     *      - and the ratio of the video source
     *
     * Add listeners on the screen and on the ratio for the current media.
     */
    private void initializeImageView() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());

        // Add an imageView in the playerHolder to display each frame of the media
        imageView.setImage(writableImage);

        playerHolder.widthProperty().addListener((observable, oldValue, newValue) ->
                fitImageViewSize(newValue.floatValue(), (float) playerHolder.getHeight())
        );

        playerHolder.heightProperty().addListener((observable, oldValue, newValue) ->
                fitImageViewSize((float) playerHolder.getWidth(), newValue.floatValue())
        );

        videoSourceRatioProperty.addListener((observable, oldValue, newValue) ->
                fitImageViewSize((float) playerHolder.getWidth(), (float) playerHolder.getHeight())
        );
    }

    /**
     * Set image dimensions to write in the player with the new values width and height.
     */
    private void fitImageViewSize(float width, float height) {
        Platform.runLater(() -> {
            float fitHeight = videoSourceRatioProperty.get() * width;
            if (fitHeight > height) {
                imageView.setFitHeight(height);
                double fitWidth = height / videoSourceRatioProperty.get();
                imageView.setFitWidth(fitWidth);
                imageView.setX((width - fitWidth) / 2);
                imageView.setY(0);
            } else {
                imageView.setFitWidth(width);
                imageView.setFitHeight(fitHeight);
                imageView.setY((height - fitHeight) / 2);
                imageView.setX(0);
            }
        });
    }

    /**
     * Set the album image dimensions to write in the player with the new values width and height.
     */
    public static void fitArtworkViewSize(ImageView artworkView, double width, double height) {
        if(artworkView.getImage() != null) {
            double ratio =  artworkView.getImage().getHeight() / artworkView.getImage().getWidth();
            double fitHeight = ratio * width;
            if(fitHeight > height) {
                double fitWidth = height / ratio;
                artworkView.setFitWidth(fitWidth);
                artworkView.setX((width - fitWidth) / 2);
                artworkView.setY(0);
            } else {
                artworkView.setFitHeight(fitHeight);
                artworkView.setY((height - fitHeight) / 2);
                artworkView.setX(0);
            }
        }
    }
}
