package sample.model;

import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.utility.Utility;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Vincent on 28/04/2016.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "28/04/2016", description = "The Playlist model contains an ArrayList of Media. This class contains methods to write/read playlist between two use of MyCast.")
public class Playlist implements Serializable {

    private ArrayList<Media> playlist;

    public Playlist() {
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

    /**
     * Save the current playlist in file
     */
    public void writePlaylist() {
        try {
            File file = new File(Constant.PATH_TO_PLAYLIST);

            if(!file.exists() && !file.createNewFile()) {
                return;
            }

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this.playlist);
            out.flush();
            out.close();
            fos.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Read previous playlist saved in file
     */
    private void readPlaylist(){
        ObjectInputStream ois = null;
        FileInputStream file;
        try {
            file = new FileInputStream(Constant.PATH_TO_PLAYLIST);
            ois = new ObjectInputStream(file);
            this.playlist = (ArrayList<Media>) ois.readObject();
            Utility.checkExistingFile(this.playlist);
        } catch (IOException | ClassNotFoundException e) {
            this.playlist = new ArrayList<>();
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
