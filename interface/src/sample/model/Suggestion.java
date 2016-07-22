package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.annotation.DocumentationAnnotation;

/**
 * Created by Pierre on 30/05/2016.
 */
@DocumentationAnnotation(author = "Pierre Lochouarn", date = "30/05/2016", description = "This is the suggestion model. It's pretty much like a Media but with some additionnal informations.")
public class Suggestion {

    String Name;
    String Type;
    String Date;
    String Director;
    String Length;



    public Suggestion(String name, String date, String director, String len, String type){

        this.Name=name;
        this.Type=type;
        this.Date=date;
        this.Director=director;
        this.Length=len;

    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(Name);
    }



    public StringProperty DateProperty() {
        return new SimpleStringProperty(Date);
    }
    public StringProperty TypeProperty() {
        return new SimpleStringProperty(Type);
    }
    public StringProperty DirectorProperty(){return new SimpleStringProperty(Director);}
    public StringProperty LengthProperty(){return new SimpleStringProperty(Length);}

    public String getName() {
        return Name;
    }
    public String getDate() {
        return Date;
    }
    public String getType() {
        return Type;
    }
    public String getDirector(){return Director ;}

    public String getLength(){return Length;}
}