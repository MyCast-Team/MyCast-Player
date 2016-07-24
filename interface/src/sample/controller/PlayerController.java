package sample.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.model.ResizablePlayer;
import sample.utility.Utility;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Control the player and bind the buttons of the player with functions
 */
public class PlayerController implements MediaPlayerEventListener {

    private ResizablePlayer resizablePlayer;
    private MediaListPlayer mediaListPlayer;
    private MediaPlayer mediaPlayer;

    private Stage stage;
    private Scene lastScene;

    private long lastTimeDisplayed;
    private String fullTime;
    private boolean isFullscreenPlayer;

    @FXML
    private VBox playerContainer;

    @FXML
    private Button previous;

    @FXML
    private Button stop;

    @FXML
    private Button play;

    @FXML
    private Button next;

    @FXML
    private Slider timeSlider;

    @FXML
    private Label timeLabel;

    @FXML
    private Button repeat;

    @FXML
    private Button resize;

    @FXML
    private Pane playerHolder;

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView artworkView;

    @FXML
    private AnchorPane player;

    private Label statusLabel;

    /* CONSTRUCTOR */
    public PlayerController() {
    }

    @FXML
    public void initialize() {
        this.stage = null;

        this.resizablePlayer = new ResizablePlayer(this.playerHolder, this.imageView, this.artworkView);
        this.mediaListPlayer = resizablePlayer.getMediaListPlayer();
        this.mediaPlayer = resizablePlayer.getMediaPlayer();
        this.mediaPlayer.addMediaPlayerEventListener(this);
        this.mediaPlayer.setRepeat(true);

        this.artworkView.setPreserveRatio(true);
        this.artworkView.setSmooth(false);

        this.lastTimeDisplayed = 0;
        this.isFullscreenPlayer = false;

        installTooltips();

        previous.setOnAction(getPreviousEventHandler());
        stop.setOnAction(getStopEventHandler());
        play.setOnAction(getPlayEventHandler());
        next.setOnAction(getNextEventHandler());
        timeSlider.valueProperty().addListener(getTimeSliderListener());
        repeat.setOnAction(getRepeatEventHandler());
        resize.setOnAction(getResizeEventHandler());
    }

    /* GETTER */
    public Slider getTimeSlider() { return timeSlider; }

    public Label getTimeLabel() { return timeLabel; }

    public long getLastTimeDisplayed() { return lastTimeDisplayed; }

    public String getFullTime() { return this.fullTime; }

    public ResizablePlayer getResizablePlayer() { return resizablePlayer; }


    /* SETTER */
    public void setTimeSlider(Slider timeSlider) { this.timeSlider = timeSlider; }

    public void setTimeLabel(Label timeLabel) { this.timeLabel = timeLabel; }

    public void setLastTimeDisplayed(long lastTimeDisplayed) { this.lastTimeDisplayed = lastTimeDisplayed; }

    public void setFullTime(String fullTime) { this.fullTime = fullTime; }

    public void setResizablePlayer(ResizablePlayer resizablePlayer) { this.resizablePlayer = resizablePlayer; }

    public void setStatusLabel(Label statusLabel) { this.statusLabel = statusLabel; }


    /************************************************************************************
     *                                  BUTTON CONTROLLER                               *
     ************************************************************************************/

    /**
     * Return an EventHandler for the Previous button
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getPreviousEventHandler() {
        return (event) -> mediaListPlayer.playPrevious();
    }

    /**
     * Return an EventHandler for the Stop button
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getStopEventHandler() {
        return (event) -> mediaListPlayer.stop();
    }

    /**
     * Return an EventHandler for the Play button
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getPlayEventHandler() {
        return (event) -> {
            if (mediaListPlayer.isPlaying()) {
                mediaListPlayer.pause();
            } else {
                mediaListPlayer.play();
            }
        };
    }

    /**
     * Return an EventHandler for the Next button
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getNextEventHandler() {
        return (event) -> mediaListPlayer.playNext();
    }

    /**
     * Return a ChangeListener for the Time slider
     * @return ChangeListener
     */
    public ChangeListener<Number> getTimeSliderListener() {
        return (observableValue, oldValue, newValue) -> {
            if (timeSlider.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                mediaPlayer.setPosition((float)(timeSlider.getValue()/100.0));
                timeLabel.setText(Utility.formatTime(mediaPlayer.getTime()) + " / " + fullTime);
                setLastTimeDisplayed(0);
            }
        };
    }

    /**
     * Return an EventHandler for the Repeat button
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getRepeatEventHandler() {
        return (event) -> {
            if(mediaPlayer.getRepeat()) {
                mediaPlayer.setRepeat(false);
                mediaListPlayer.setMode(MediaListPlayerMode.DEFAULT);
                repeat.setGraphic(new ImageView(new Image("icons/noRepeat.png")));
            } else {
                mediaPlayer.setRepeat(true);
                mediaListPlayer.setMode(MediaListPlayerMode.LOOP);
                repeat.setGraphic(new ImageView(new Image("icons/repeat.png")));
            }
        };
    }

    /**
     * Return an EventHandler for the Resize button
     * @return EventHandler
     */
    public EventHandler<ActionEvent> getResizeEventHandler() {
        return (event)-> {
            if(stage == null) {
                stage = (Stage) play.getScene().getWindow();
            }
            boolean isFullscreenStage = stage.isFullScreen();
            if(isFullscreenPlayer) {
                player.getChildren().add(playerContainer);
                stage.setScene(lastScene);
                stage.show();
                isFullscreenPlayer = false;
                stage.setFullScreen(isFullscreenStage);
            } else {
                mediaListPlayer.play();
                play.setGraphic(new ImageView(new Image("icons/pause.png")));
                lastScene = stage.getScene();
                stage.setScene(new Scene(new AnchorPane(playerContainer)));
                stage.show();
                isFullscreenPlayer = true;
                stage.setFullScreen(isFullscreenStage);
            }
        };
    }

    /**
     * Set tooltips on each button
     */
    public void installTooltips(){
        this.play.setTooltip(new Tooltip("Play/Pause the current media"));
        this.previous.setTooltip(new Tooltip("Play the previous media"));
        this.stop.setTooltip(new Tooltip("Stop the reading of the media"));
        this.next.setTooltip(new Tooltip("Play the next media"));
        this.repeat.setTooltip(new Tooltip("Set the repetition of the playlist"));
        this.resize.setTooltip(new Tooltip("Change the player mode to full screen"));
        this.timeSlider.setTooltip(new Tooltip("Set the media time location"));
    }

    /************************************************************************************
     *                  IMPLEMENTS MediaPlayerEventListener METHODS                     *
     ************************************************************************************/

    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {

        URL url;
        File file = null;
        String path;
        MediaMeta metaInfo;
        String artworkUrl;

        try {
            url = new URL(mrl);
            file = new File(url.toURI());
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
            file = new File(mrl);
        } finally {
            path = (file == null) ? mrl : file.getPath();
            if(path.startsWith("file:")) {
                path = path.substring(5);
            }
        }

        metaInfo = new MediaPlayerFactory().getMediaMeta(path, true);

        // Print the album image of the current media if it is a music
        if(Utility.audioExtensionIsSupported(path.substring(path.lastIndexOf(".")+1))) {
            artworkUrl = metaInfo.getArtworkUrl();

            if (artworkUrl != null) {
                artworkView.setImage(new Image(artworkUrl));
                Pane pane = (Pane) artworkView.getParent();
                ResizablePlayer.fitArtworkViewSize(artworkView, pane.getWidth(), pane.getHeight());
            }
            imageView.setVisible(false);
            artworkView.setVisible(true);
        } else {
            artworkView.setVisible(false);
            imageView.setVisible(true);
        }

        // Reinitialize the time slider and the time label, and print the title of the current media in the status bar
        Platform.runLater(() -> {
            timeSlider.setValue(0.0);
            timeLabel.setText(Utility.formatTime(mediaPlayer.getTime()) + " / " + fullTime);
            setLastTimeDisplayed(0);

            String text = ((metaInfo.getArtist() != null) ? metaInfo.getArtist() + " - " : "") + metaInfo.getTitle();
            statusLabel.setText(text);
        });
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {}

    @Override
    public void buffering(MediaPlayer mediaPlayer, float newCache) {}

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        Platform.runLater(()-> play.setGraphic(new ImageView(new Image("icons/pause.png"))));
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        Platform.runLater(()-> play.setGraphic(new ImageView(new Image("icons/play.png"))));
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        Platform.runLater(() -> {
            timeSlider.setValue(0.0);
            timeLabel.setText(Utility.formatTime(mediaPlayer.getTime()) + " / " + fullTime);
            setLastTimeDisplayed(0);
            play.setGraphic(new ImageView(new Image("icons/play.png")));

            statusLabel.setText("No playing item");
        });
    }

    @Override
    public void forward(MediaPlayer mediaPlayer) {}

    @Override
    public void backward(MediaPlayer mediaPlayer) {}

    @Override
    public void finished(MediaPlayer mediaPlayer) {}

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        Platform.runLater(() -> {
            long currentTime = mediaPlayer.getTime();
            // Refresh time to display each second
            if (currentTime >= lastTimeDisplayed + 1000) {
                timeLabel.setText(Utility.formatTime(mediaPlayer.getTime()) + " / " + fullTime);
                lastTimeDisplayed = currentTime;
            }
        });
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
        if (!timeSlider.isValueChanging()) {
            timeSlider.setValue(newPosition * 100.0);
        }
    }

    @Override
    public void seekableChanged(MediaPlayer mediaPlayer, int newSeekable) {}

    @Override
    public void pausableChanged(MediaPlayer mediaPlayer, int newPausable) {}

    @Override
    public void titleChanged(MediaPlayer mediaPlayer, int newTitle) {}

    @Override
    public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {}

    @Override
    public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
        this.fullTime = Utility.formatTime(newLength);
    }

    @Override
    public void videoOutput(MediaPlayer mediaPlayer, int newCount) {}

    @Override
    public void scrambledChanged(MediaPlayer mediaPlayer, int newScrambled) {}

    @Override
    public void elementaryStreamAdded(MediaPlayer mediaPlayer, int type, int id) {}

    @Override
    public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int type, int id) {}

    @Override
    public void elementaryStreamSelected(MediaPlayer mediaPlayer, int type, int id) {}

    @Override
    public void error(MediaPlayer mediaPlayer) {}

    @Override
    public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {}

    @Override
    public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {}

    @Override
    public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {}

    @Override
    public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {}

    @Override
    public void mediaFreed(MediaPlayer mediaPlayer) {}

    @Override
    public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {}

    @Override
    public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t item) {}

    @Override
    public void newMedia(MediaPlayer mediaPlayer) {}

    @Override
    public void subItemPlayed(MediaPlayer mediaPlayer, int subItemIndex) {}

    @Override
    public void subItemFinished(MediaPlayer mediaPlayer, int subItemIndex) {}

    @Override
    public void endOfSubItems(MediaPlayer mediaPlayer) {}
}
