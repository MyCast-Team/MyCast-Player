package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Pierre on 30/05/2016.
 */
public class Suggestion {

    String Name;
    String Type;
    String Date;
    String  Director;
    String Length;
    String Producer;


    public Suggestion(String name, String date, String director, String len, String type, String prod){

        this.Name=name;
        this.Type=type;
        this.Date=date;
        this.Director=director;
        this.Length=len;
        this.Producer=prod;

    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(Name);
    }

    public StringProperty producerProperty() {
        return new SimpleStringProperty(Producer);
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
    public String getProducer() {
        return Producer;
    }
    public String getLength(){return Length;}
}