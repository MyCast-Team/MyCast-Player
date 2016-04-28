package sample.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

/**
 * Control the player and bind the buttons of the player with functions
 */
public class PlayerController implements MediaPlayerEventListener {

    private MediaListPlayer mediaListPlayer;
    private MediaPlayer mediaPlayer;
    private Stage stage;
    private ImageView image;

    private Button previous;
    private Button stop;
    private Button play;
    private Button next;
    private Slider timeSlider;
    private Label timeLabel;
    private Button repeat;
    private Button resize;

    private long lastTimeDisplayed;
    private String fullTime;

    /* CONSTRUCTOR */
    public PlayerController(MediaListPlayer mediaListPlayer, MediaPlayer mediaPlayer, Stage stage, AnchorPane playerContainer) {

        this.mediaListPlayer = mediaListPlayer;
        this.mediaPlayer = mediaPlayer;
        this.stage = stage;
        /*
        VBox vBox = (VBox) playerContainer.lookup("#playerContainer");
        BorderPane bp = (BorderPane) vBox.getChildren().get(0);
        Pane playerHolder = (Pane) bp.getChildren().get(0);
        image = (ImageView) playerHolder.getChildren().get(0);
        */
        this.previous = (Button) playerContainer.lookup("#previous");
        this.stop = (Button) playerContainer.lookup("#stop");
        this.play = (Button) playerContainer.lookup("#play");
        this.next = (Button) playerContainer.lookup("#next");
        this.timeSlider = (Slider) playerContainer.lookup("#timeSlider");
        this.timeLabel = (Label) playerContainer.lookup("#timeLabel");
        this.repeat = (Button) playerContainer.lookup("#repeat");
        this.resize = (Button) playerContainer.lookup("#resize");

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


    /* SETTER */
    public void setTimeSlider(Slider timeSlider) { this.timeSlider = timeSlider; }

    public void setTimeLabel(Label timeLabel) { this.timeLabel = timeLabel; }

    public void setLastTimeDisplayed(long lastTimeDisplayed) { this.lastTimeDisplayed = lastTimeDisplayed; }

    public void setFullTime(String fullTime) { this.fullTime = fullTime; }


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
                    mediaPlayer.pause();
                    play.setGraphic(new ImageView(new Image("./img/play.png")));
                } else {
                    mediaPlayer.play();
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
                if(stage.isFullScreen())
                    stage.setFullScreen(false);
                else
                    stage.setFullScreen(true);
            }
        });
    }


    /* OVERRIDE MediaPlayerEventListener methods */
    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {}

    @Override
    public void opening(MediaPlayer mediaPlayer) {}

    @Override
    public void buffering(MediaPlayer mediaPlayer, float newCache) {}

    @Override
    public void playing(MediaPlayer mediaPlayer) {}

    @Override
    public void paused(MediaPlayer mediaPlayer) {}

    @Override
    public void stopped(MediaPlayer mediaPlayer) {}

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
