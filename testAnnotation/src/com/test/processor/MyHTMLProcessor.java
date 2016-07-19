package com.test.processor;

import com.test.annotation.MyAnnotation;

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
@SupportedAnnotationTypes(value = { "com.test.annotation.MyAnnotation" })
//Défini quelle version de source gérer, ici Java 8
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
        html.append("<html>\n");
        html.append("<header>\n");
        html.append("	<link rel=\"stylesheet\" type=\"text/css\" href=\"mystyle.css\">\n");
		html.append("</header>\n");
        html.append("<body>\n");
        html.append("	<table>\n");
        html.append("	<tr>\n");
        html.append("	<td>Type</td>\n");
        html.append("	<td>Nom Classe</td>\n");
        html.append("	<td>Auteur</td>\n");
        html.append("	<td>Date</td>\n");
        html.append("	<td>Description</td>\n");
        html.append("	</tr>\n");
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
                    html.append("	<tr>\n");
                    html.append("	<td>" + element.getKind() + "</td>\n");
                    html.append("	<td>" + element.getSimpleName() + "</td>\n");
                    html.append("	<td>" + desc.author() + "</td>\n");
                    html.append("	<td>" + desc.date() + "</td>\n");
                    html.append("	<td>" + desc.description()+ "</td>\n");
                    html.append("	</tr>\n");
                }
            }
        }
        System.out.println("Fin du traitement HTML");

        //Génération du fichier HTML
        genererHTML();
        return true;
    }

    private void genererHTML(){

        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
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
