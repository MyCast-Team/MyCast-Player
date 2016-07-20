package sample.model;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.nio.ByteBuffer;

/**
 * Class of creation and control of the vlcj player.
 */
public class ResizablePlayer {

    private DirectMediaPlayerComponent mediaPlayerComponent;
    private MediaListPlayer mediaListPlayer;
    private MediaList playlist;
    private MediaPlayer mediaPlayer;

    private ImageView artworkView;
    private ImageView imageView;
    private WritableImage writableImage;
    private WritablePixelFormat<ByteBuffer> pixelFormat;

    private Pane playerHolder;
    private FloatProperty videoSourceRatioProperty;

    private Playlist detailedPlaylist;

    /* CONSTRUCTOR */
    public ResizablePlayer(Pane playerHolder, ImageView imageView, ImageView artworkView) {

        // Initialisation of the components
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);

        this.playerHolder = playerHolder;
        this.imageView = imageView;
        this.artworkView = artworkView;

        initializeImageView();

        // Set the different component of the player (mediaListPlayer, mediaList, mediaPlayer)
        mediaPlayerComponent = new CanvasPlayerComponent(writableImage, pixelFormat, videoSourceRatioProperty);
        mediaListPlayer = mediaPlayerComponent.getMediaPlayerFactory().newMediaListPlayer();

        playlist = mediaPlayerComponent.getMediaPlayerFactory().newMediaList();
        mediaListPlayer.setMediaList(playlist);

        mediaPlayer = mediaPlayerComponent.getMediaPlayer();
        mediaListPlayer.setMediaPlayer(mediaPlayer);
    }


    /* GETTER */
    public DirectMediaPlayerComponent getMediaPlayerComponent() { return mediaPlayerComponent; }

    public MediaListPlayer getMediaListPlayer() { return mediaListPlayer; }

    public MediaList getPlaylist() { return playlist; }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }

    public Pane getPlayerHolder() { return playerHolder; }

    public Playlist getDetailedPlaylist() { return detailedPlaylist; }

    /* SETTER */
    public void setPlaylist(Playlist playlist) {
        this.detailedPlaylist = playlist;
        for(Media m : playlist.getPlaylist()) {
            this.playlist.addMedia(m.getPath());
        }
    }

    public void release() {
        this.mediaPlayerComponent.release(true);
        this.mediaListPlayer.release();
        this.mediaPlayer.release();
    }

    /**
     * initialize the type of image (size, ratio) to write in the player, accordingly with :
     *      - the dimensions of the screen
     *      - and the ratio of the video source
     *
     * Add listeners on the screen and on the ratio for the current media.
     */
    public void initializeImageView() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());

        // Add an imageView in the playerHolder to display each frame of the media
        imageView.setImage(writableImage);

        playerHolder.widthProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize(newValue.floatValue(), (float) playerHolder.getHeight());
        });

        playerHolder.heightProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playerHolder.getWidth(), newValue.floatValue());
        });

        videoSourceRatioProperty.addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playerHolder.getWidth(), (float) playerHolder.getHeight());
        });
    }

    /**
     * Set image dimensions to write in the player with the new values width and height.
     * @param width
     * @param height
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
            }
            else {
                imageView.setFitWidth(width);
                imageView.setFitHeight(fitHeight);
                imageView.setY((height - fitHeight) / 2);
                imageView.setX(0);
            }

            if(artworkView.getImage() != null) {
                artworkView.setX(width/2 - artworkView.getImage().getWidth()/2);
                artworkView.setY(height/2 - artworkView.getImage().getHeight()/2);
            }
        });
    }
}
