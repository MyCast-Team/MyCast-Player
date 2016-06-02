package sample.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.model.ResizablePlayer;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaListItem;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import sample.model.Media;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Control the player and bind the buttons of the player with functions
 */
public class PlayerController implements MediaPlayerEventListener {

    private MediaListPlayer mediaListPlayer;
    private MediaPlayer mediaPlayer;
    private Stage stage;

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

    private long lastTimeDisplayed;

    private String fullTime;

    private ResizablePlayer resizablePlayer;


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

        this.artworkView.setPreserveRatio(true);
        this.artworkView.setSmooth(true);

        this.lastTimeDisplayed = 0;

        addPreviousListener();
        addStopListener();
        addPlayListener();
        addNextListener();
        addTimeSliderListener();
        addRepeatListener();
        addResizeListener();
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


    /* BUTTON CONTROLLER */
    public void addPreviousListener() {
        previous.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //image.setImage(new Image("/Users/thomasfouan/Desktop/image.png"));

                mediaPlayer.setPosition(0.0f);
                timeSlider.setValue(0.0);
                timeLabel.setText(getStringTime(mediaPlayer));
                setLastTimeDisplayed(0);
                mediaListPlayer.playPrevious();
            }
        });
    }

    public void addStopListener() {
        stop.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayer.canPause()) {
                    mediaPlayer.setPosition(0.0f);
                    timeSlider.setValue(0.0);
                    timeLabel.setText(getStringTime(mediaPlayer));
                    setLastTimeDisplayed(0);
                    play.setGraphic(new ImageView(new Image("./img/play.png")));
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                }
            }
        });
    }

    public void addPlayListener() {
        play.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayer.isPlaying()) {
                    //mediaPlayer.pause();
                    mediaListPlayer.pause();
                    play.setGraphic(new ImageView(new Image("./img/play.png")));
                } else {
                    mediaListPlayer.play();
                    //mediaPlayer.play();
                    play.setGraphic(new ImageView(new Image("./img/pause.png")));
                }
            }
        });
    }

    public void addNextListener() {
        next.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediaPlayer.setPosition(0.0f);
                timeSlider.setValue(0.0);
                timeLabel.setText(getStringTime(mediaPlayer));
                setLastTimeDisplayed(0);
                mediaListPlayer.playNext();
                //image.setImage(new Image("/img/resize.png"));
                //WritableImage wi = (WritableImage) image.getImage();
                //wi.getPixelWriter();
            }
        });
    }

    public void addTimeSliderListener() {
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mediaPlayer.setPosition((float)(timeSlider.getValue()/100.0));
                    timeLabel.setText(getStringTime(mediaPlayer));
                    setLastTimeDisplayed(0);
                }
            }
        });
    }

    public void addRepeatListener() {
        repeat.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayer.getRepeat()) {
                    mediaPlayer.setRepeat(false);
                    repeat.setGraphic(new ImageView(new Image("./img/random.png")));
                } else {
                    mediaPlayer.setRepeat(true);
                    repeat.setGraphic(new ImageView(new Image("./img/repeat.png")));
                }
            }
        });
    }

    public void addResizeListener() {
        resize.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(stage == null) {
                    stage = (Stage) play.getScene().getWindow();
                }
                if(stage.isFullScreen())
                    stage.setFullScreen(false);
                else
                    stage.setFullScreen(true);
            }
        });
    }


    /* OVERRIDE MediaPlayerEventListener methods */
    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
        String url = mrl.substring(mrl.indexOf("/"));

        MediaMeta metaInfo = new MediaPlayerFactory().getMediaMeta(url, true);
        String artworkUrl = metaInfo.getArtworkUrl();

        boolean isMusic = false;
        for(String ext : PlaylistController.EXTENSIONS_AUDIO) {
            if(ext.equals(url.substring(url.lastIndexOf(".")+1))) {
                isMusic = true;
                break;
            }
        }

        if(isMusic) {
            if (artworkUrl != null) {
                artworkView.setImage(new Image(artworkUrl));
                artworkView.setX(playerHolder.getWidth()/2 - artworkView.getImage().getWidth()/2);
                artworkView.setY(playerHolder.getHeight()/2 - artworkView.getImage().getHeight()/2);
            }
            imageView.setVisible(false);
            artworkView.setVisible(true);
        } else {
            artworkView.setVisible(false);
            imageView.setVisible(true);
        }
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {}

    @Override
    public void buffering(MediaPlayer mediaPlayer, float newCache) {}

    @Override
    public void playing(MediaPlayer mediaPlayer) {}

    @Override
    public void paused(MediaPlayer mediaPlayer) {}

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        Platform.runLater(() -> {
            mediaPlayer.setPosition(0.0f);
            timeSlider.setValue(0.0);
            timeLabel.setText(getStringTime(mediaPlayer));
            setLastTimeDisplayed(0);
            play.setGraphic(new ImageView(new Image("./img/play.png")));
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
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
        if (timeLabel != null) {
            long currentTime = mediaPlayer.getTime();
            // Refresh time to display each second
            if (currentTime >= lastTimeDisplayed + 1000) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        timeLabel.setText(getStringTime(mediaPlayer));
                        lastTimeDisplayed = currentTime;
                    }
                });
            }
        }
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
        if (timeSlider != null && !timeSlider.isValueChanging()) {
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
        this.fullTime = formatTime(newLength);
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


    /* UTILITIES */
    public String getStringTime(MediaPlayer mediaPlayer) {
        return formatTime(mediaPlayer.getTime())+" / "+ fullTime;
    }

    private String formatTime(long time) {

        int hours, minutes;

        // milliseconds to seconds
        time /= 1000;

        hours = (int) time/3600;
        time -= hours*3600;

        minutes = (int) time/60;
        time -= minutes*60;

        return String.format("%02d:%02d:%02d", hours, minutes, time);
    }
}
