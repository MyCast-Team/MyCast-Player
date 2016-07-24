package sample;

import sample.annotation.DocumentationAnnotation;

/**
 * Created by thomasfouan on 30/05/16.
 */

@DocumentationAnnotation(author = "Thomas", date = "19/07/2016", description = "Ceci est une description de classe")
public class Main {

    public static void main(String[] args) {
        System.exit(0);
    }
}
