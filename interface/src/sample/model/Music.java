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
    String duration;
    // Il faut regarder les autres informations dans ID3v2 (pochette album, ...)

    public Music(String title, String author, String duration) {
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

    public String getDuration() { return duration; }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public StringProperty durationProperty() {
        return new SimpleStringProperty(duration);
    }
}
