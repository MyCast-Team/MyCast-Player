package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.annotation.DocumentationAnnotation;

/**
 * Created by Pierre on 30/05/2016.
 */
@DocumentationAnnotation(author = "Pierre Lochouarn", date = "30/05/2016", description = "This is the suggestion model. It's pretty much like a Media but with some additional information.")
public class Suggestion {

    private String name;
    private String date;
    private String director;
    private String length;
    private String type;

    public Suggestion(String name, String date, String director, String len, String type){

        this.name = name;
        this.date = date;
        this.director = director;
        this.length = len;
        this.type = type;
    }

    public void setName(String name) { this.name = name; }

    public void setDirector(String director) { this.director = director; }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
    public StringProperty DateProperty() {
        return new SimpleStringProperty(date);
    }
    public StringProperty TypeProperty() {
        return new SimpleStringProperty(type);
    }
    public StringProperty DirectorProperty(){return new SimpleStringProperty(director);}
    public StringProperty LengthProperty(){return new SimpleStringProperty(length);}

    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getType() {
        return type;
    }
    public String getDirector(){return director ;}
    public String getLength(){return length;}
}