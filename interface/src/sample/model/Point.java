package sample.model;

import sample.annotation.DocumentationAnnotation;

/**
 * Created by Vincent on 11/05/2016.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "11/05/2016", description = "The class Point is used to to display components in our GridPane.")
public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
