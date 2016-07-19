package sample.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.io.File;

/**
 * Control the player and bind the buttons of the player with functions
 */
public class PlayerController implements MediaPlayerEventListener {

    private MediaListPlayer mediaListPlayer;
    private MediaPlayer mediaPlayer;
    private Stage stage;

    private Scene lastScene;
    private long lastTimeDisplayed;
    private String fullTime;
    private ResizablePlayer resizablePlayer;
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

        this.artworkView.setPreserveRatio(true);
        this.artworkView.setSmooth(true);

        this.lastTimeDisplayed = 0;
        this.isFullscreenPlayer = false;

        installTooltips();

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

    public void setStatusLabel(Label statusLabel) { this.statusLabel = statusLabel; }


    /* BUTTON CONTROLLER */
    public void addPreviousListener() {
        previous.addEventHandler(ActionEvent.ACTION, (event) -> {
            mediaListPlayer.playPrevious();
        });
    }

    public void addStopListener() {
        stop.addEventHandler(ActionEvent.ACTION, (event) -> {
            mediaListPlayer.stop();
        });
    }

    public void addPlayListener() {
        play.addEventHandler(ActionEvent.ACTION, (event) -> {
            if(mediaListPlayer.isPlaying()) {
                mediaListPlayer.pause();
            } else {
                mediaListPlayer.play();
            }
        });
    }

    public void addNextListener() {
        next.addEventHandler(ActionEvent.ACTION, (event) -> {
            mediaListPlayer.playNext();
        });
    }

    public void addTimeSliderListener() {
        timeSlider.valueProperty().addListener((ov) -> {
            if (timeSlider.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                mediaPlayer.setPosition((float)(timeSlider.getValue()/100.0));
                timeLabel.setText(getStringTime(mediaPlayer));
                setLastTimeDisplayed(0);
            }
        });
    }

    public void addRepeatListener() {
        repeat.addEventHandler(ActionEvent.ACTION, (event) -> {
            if(mediaPlayer.getRepeat()) {
                mediaPlayer.setRepeat(false);
                repeat.setGraphic(new ImageView(new Image("./img/random.png")));
            } else {
                mediaPlayer.setRepeat(true);
                repeat.setGraphic(new ImageView(new Image("./img/repeat.png")));
            }
        });
    }

    public void addResizeListener() {
        resize.addEventHandler(ActionEvent.ACTION, (event)-> {
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
                play.setGraphic(new ImageView(new Image("./img/pause.png")));
                lastScene = stage.getScene();
                stage.setScene(new Scene(new AnchorPane(playerContainer)));
                stage.show();
                isFullscreenPlayer = true;
                stage.setFullScreen(isFullscreenStage);
            }
        });
    }

    public void installTooltips(){
        this.play.setTooltip(new Tooltip("Play/Pause the current media"));
        this.previous.setTooltip(new Tooltip("Play the previous media"));
        this.stop.setTooltip(new Tooltip("Stop the reading of the media"));
        this.next.setTooltip(new Tooltip("Play the next media"));
        this.repeat.setTooltip(new Tooltip("Set the repetition of the playlist"));
        this.resize.setTooltip(new Tooltip("Change the player mode to full screen"));
        this.timeSlider.setTooltip(new Tooltip("Set the media time location"));
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

        Platform.runLater(() -> {
            timeSlider.setValue(0.0);
            timeLabel.setText(getStringTime(mediaPlayer));
            setLastTimeDisplayed(0);

            String path = new File(mrl).getPath();
            MediaMeta meta = new MediaPlayerFactory().getMediaMeta(path.substring(path.indexOf(":")+1), true);
            String text = ((meta.getArtist() != null) ? meta.getArtist() + " - " : "") + meta.getTitle();
            statusLabel.setText(text);
        });
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {}

    @Override
    public void buffering(MediaPlayer mediaPlayer, float newCache) {}

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        Platform.runLater(()-> {
            play.setGraphic(new ImageView(new Image("./img/pause.png")));
        });
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        Platform.runLater(()-> {
            play.setGraphic(new ImageView(new Image("./img/play.png")));
        });
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        Platform.runLater(() -> {
            timeSlider.setValue(0.0);
            timeLabel.setText(getStringTime(mediaPlayer));
            setLastTimeDisplayed(0);
            play.setGraphic(new ImageView(new Image("./img/play.png")));

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
                timeLabel.setText(getStringTime(mediaPlayer));
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

    public static String formatTime(long time) {

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
