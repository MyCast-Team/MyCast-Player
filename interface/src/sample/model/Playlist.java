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
        Music m1 = new Music("Daft Punk", "Around the world", "3:30");
        Music m2 = new Music("Daft Punk", "Around the world", "3:30");
        try{
            this.playlist.add(m1);
            this.playlist.add(m2);
        } catch (NullPointerException e){
            System.out.println("exception");
        }
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
            System.out.println("I've writen the file");
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
            System.out.println(this.playlist);
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
}
