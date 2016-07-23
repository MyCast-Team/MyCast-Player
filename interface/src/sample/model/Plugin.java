package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.annotation.DocumentationAnnotation;

/**
 * Created by Pierre on 30/05/2016.
 */
@DocumentationAnnotation(author = "Pierre Lochouarn", date = "30/05/2016", description = "This is the model for a Plugin. It contains several informations that will be displayed in the plugin component.")
public class Plugin {

    String name;
    String author;
    String date;
    String id;

    public Plugin(String name, String author, String date, String id){
        this.name = name;
        this.author = author;
        this.date = date;
        this.id = id;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }
    public StringProperty AuthorProperty() {
        return new SimpleStringProperty(author);
    }
    public StringProperty DateProperty() {
        return new SimpleStringProperty(date);
    }
    public StringProperty IdProperty(){return new SimpleStringProperty(id);}

    public String getAuthor() {
        return author;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getId(){return id;}
}
