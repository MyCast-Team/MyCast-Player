package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.annotation.DocumentationAnnotation;
import sample.utility.Utility;

import java.io.Serializable;

/**
 * Created by Vincent on 07/03/2016.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "07/03/2016", description = "The Media class contains the main informations for a Media : it can be a video or a music. We use this template to manage our list of medias like Mediacase or Playlist.")
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
        return new SimpleStringProperty(Utility.formatTime(duration));
    }
    public StringProperty genreProperty() { return new SimpleStringProperty(genre); }
    public StringProperty dateProperty() { return new SimpleStringProperty(release); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media)) return false;

        Media media = (Media) o;

        if (duration != media.duration) return false;
        if (path != null ? !path.equals(media.path) : media.path != null) return false;
        if (title != null ? !title.equals(media.title) : media.title != null) return false;
        if (author != null ? !author.equals(media.author) : media.author != null) return false;
        if (genre != null ? !genre.equals(media.genre) : media.genre != null) return false;
        return release != null ? release.equals(media.release) : media.release == null;
    }
}
