import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

/**
 * Created by thomasfouan on 17/03/2016.
 */
public class PlayerController implements MediaPlayerEventListener {

    private Slider timeSlider;
    private Label timeLabel;
    private long lastTimeDisplayed;
    private String fullTime;

    /* CONSTRUCTOR */
    public PlayerController(Slider timeSlider, Label timeLabel) {

        this.timeSlider = timeSlider;
        this.timeLabel = timeLabel;
        this.lastTimeDisplayed = 0;
    }


    /* GETTER */
    public Slider getTimeSlider() {
        return timeSlider;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public long getLastTimeDisplayed() {
        return lastTimeDisplayed;
    }

    public String getFullTime() {
        return this.fullTime;
    }


    /* SETTER */
    public void setFullTime(String fullTime) {
        this.fullTime = fullTime;
    }

    public void setTimeSlider(Slider timeSlider) {
        this.timeSlider = timeSlider;
    }

    public void setTimeLabel(Label timeLabel) {
        this.timeLabel = timeLabel;
    }

    public void setLastTimeDisplayed(long lastTimeDisplayed) {
        this.lastTimeDisplayed = lastTimeDisplayed;
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
