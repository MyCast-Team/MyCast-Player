package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Vincent on 07/03/2016.
 */
public class Media implements Serializable {

    private String path;

    private String title;

    private String author;

    private long duration;

    private String genre;

    private String release;

    public Media(String path, String title, String author, long duration, String release, String genre) {
        if(path != null)
            this.path = path;

        if(title != null)
            this.title = title;
        else this.title = "";

        if(author != null)
            this.author = author;
        else this.author = "";

        this.duration = duration;

        if(release != null)
            this.release = release;
        else this.release = "";

        if(genre != null)
            this.genre = genre;
        else this.genre = "";
    }

    /* GETTER */
    public String getPath() { return path; }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getDuration() { return duration; }

    public String getGenre() {
        return genre;
    }

    public String getRelease() {
        return release;
    }

    /* SETTER */
    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public StringProperty titleProperty() {
        return new SimpleStringProperty(title);
    }

    public StringProperty authorProperty() {
        return new SimpleStringProperty(author);
    }

    public StringProperty durationProperty() {
        return new SimpleStringProperty(createStringFromLong(duration));
    }

    public StringProperty genreProperty() { return new SimpleStringProperty(genre); }

    public StringProperty dateProperty() { return new SimpleStringProperty(release); }

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

    @Override
    public boolean equals(Object obj){

        if(!(obj instanceof Media))
            return false;

        Media m = (Media) obj;

        if(!this.title.equals(m.title))
            return false;
        if(!this.author.equals(m.author))
            return false;
        if(this.duration != m.duration)
            return false;
        if(!this.genre.equals(m.genre))
            return false;
        if(!this.release.equals(m.release))
            return false;

        return true;
    }
}
