package sample.model;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Vincent on 28/04/2016.
 */
public class Playlist implements Serializable {
    private ArrayList<Media> playlist;
    final String path = "./res/playlist.ser";

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

    public void writePlaylist(){
        try {
            File file = new File(path);
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
            FileOutputStream fileOut = new FileOutputStream(path);
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
            FileInputStream fichier = new FileInputStream(path);
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
