package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Created by Vincent on 07/03/2016.
 */
public class Music extends Media implements Serializable {
    String title;
    String author;
    long duration;
    // Il faut regarder les autres informations dans ID3v2 (pochette album, ...)

    public Music(String title, String author, long duration) {
        this.title = title;
        this.author = author;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StringProperty titleProperty() {
        return new SimpleStringProperty(title);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public StringProperty authorProperty() {
        return new SimpleStringProperty(author);
    }

    public long getDuration() { return duration; }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public StringProperty durationProperty() {
        return new SimpleStringProperty(createStringFromLong(duration));
    }

    public String createStringFromLong(long time){
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
