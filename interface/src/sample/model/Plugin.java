package sample.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sample.annotation.DocumentationAnnotation;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Pierre on 30/05/2016.
 */
@DocumentationAnnotation(author = "Pierre Lochouarn", date = "30/05/2016", description = "This is the model for a Plugin. It contains several informations that will be displayed in the plugin component.")
public class Plugin {

    String Name;
    String Author;
    String Date;
    String  Id;

    public Plugin(String name, String author,String date,String Id){
        this.Name=name;
        this.Author=author;
        this.Date=date;
        this.Id=Id;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(Name);
    }
    public StringProperty AuthorProperty() {
        return new SimpleStringProperty(Author);
    }
    public StringProperty DateProperty() {
        return new SimpleStringProperty(Date);
    }
    public StringProperty IdProperty(){return new SimpleStringProperty(Id);}
    public String getAuthor() {
        return Author;
    }
    public String getName() {
        return Name;
    }
    public String getDate() {
        return Date;
    }
    public String getId(){return Id ;}
}
