package sample.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Vincent on 07/03/2016.
 */
public class Music implements Media {
    // Les commentaires représentent les infos ID3 (metadata mp3)
    StringProperty title; //Offset 3, longueur 30 octets
    StringProperty author; //Offset 33, longueur 30 octets
    StringProperty duration;
    // Il faut regarder les autres informations dans ID3v2 (pochette album, ...)

    public Music(String title, String author, String duration) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.duration = new SimpleStringProperty(duration);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public String getDuration() { return duration.get(); }

    public StringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }
}
