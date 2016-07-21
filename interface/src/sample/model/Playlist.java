package sample.model;

import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Vincent on 28/04/2016.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "28/04/2016", description = "The Playlist model contains an ArrayList of Media. This class contains methods to write/read playlist between two use of MyShare.")
public class Playlist implements Serializable {
    private ArrayList<Media> playlist;

    public Playlist(){
        playlist = new ArrayList<>();
        readPlaylist();
    }

    public ArrayList<Media> getPlaylist(){
        return playlist;
    }

    public void addMedia(Media media){
        playlist.add(media);
    }

    public void removeMedia(Media media) { playlist.remove(media); }

    public void writePlaylist(){
        try {
            File file = new File(Constant.PATH_TO_PLAYLIST);
            if(!file.exists()){
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write("");
                    writer.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            FileOutputStream fileOut = new FileOutputStream(Constant.PATH_TO_PLAYLIST);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.playlist);
            out.flush();
            out.close();
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    public void readPlaylist(){
        ObjectInputStream ois = null;
        try {
            FileInputStream fichier = new FileInputStream(Constant.PATH_TO_PLAYLIST);
            ois = new ObjectInputStream(fichier);
            this.playlist = (ArrayList<Media>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            writePlaylist();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void reset(){
        this.playlist.clear();
    }
}
