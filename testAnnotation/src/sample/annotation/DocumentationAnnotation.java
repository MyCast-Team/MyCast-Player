package sample.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author thomasfouan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface DocumentationAnnotation {
    
    String author();
    String date();
    String description();
}
