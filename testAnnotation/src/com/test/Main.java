package com.test;

import com.test.annotation.MyAnnotation;

/**
 * Created by thomasfouan on 30/05/16.
 */

@MyAnnotation(author = "Thomas", date = "19/07/2016", description = "Ceci est une description de classe")
public class Main {

    public static void main(String[] args) {
        System.out.println("Coucou, c'est la méthode principale du programme.");
        System.exit(0);
    }
}
