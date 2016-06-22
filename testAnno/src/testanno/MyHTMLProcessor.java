/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testanno;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author thomasfouan
 */
//Permet de spécifier les annotations à traiter
@SupportedAnnotationTypes(value = { "testanno.MyAnnotation" })
//Défini quelle version de source gérer, ici je code en Java 8
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyHTMLProcessor extends AbstractProcessor {
    
    FileOutputStream fw = null;
    StringBuilder html;
    File htmlFile;
    boolean firstLoop;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        
        firstLoop = true;
        
        htmlFile = new File("/Users/thomasfouan/Desktop/MyDescription.html");

        try {
            fw = new FileOutputStream(htmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        html = new StringBuilder();
        html.append("<html>");
        html.append("<body>");
        html.append("<table>");

        html.append("<tr>");
        html.append("<td style=\"border:1px solid black\">Type</td>");
        html.append("<td style=\"border:1px solid black\">Nom Classe</td>");
        html.append("<td style=\"border:1px solid black\">Auteur</td>");
        html.append("<td style=\"border:1px solid black\">Date</td>");
        html.append("<td style=\"border:1px solid black\">Description</td>");
        html.append("</tr>");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        
        if(!firstLoop) {
            return true;
        }
        
        System.out.println("Début du traitement HTML !");

        for (TypeElement te : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(te)) {
                String name = element.getClass().toString();

                MyAnnotation desc = element.getAnnotation(MyAnnotation.class);
                
                if (desc != null) {
                    html.append("<tr>");
                    html.append("<td style=\"border:1px solid black\">" + element.getKind() + "</td>");
                    html.append("<td style=\"border:1px solid black\">" + element.getSimpleName() + "</td>");
                    html.append("<td style=\"border:1px solid black\">" + desc.author() + "</td>");
                    html.append("<td style=\"border:1px solid black\">" + desc.date() + "</td>");
                    html.append("<td style=\"border:1px solid black\">" + desc.description()+ "</td>");
                    html.append("</tr>");
                }
            }
        }
        System.out.println("Fin du traitement HTML");

        //Génération du fichier HTML
        genererHTML();
        return true;
    }

    private void genererHTML(){

        html.append("</table>");
        html.append("</body>");
        html.append("</html>");
        
        try {
            fw.write(html.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            firstLoop = false;
            try{
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
