package sample.model;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Vincent on 14/06/2016.
 */
public class Mediacase {

    private ArrayList<Media> videocase;
    private ArrayList<Media> musiccase;
    final String videopath = "./res/videocase.ser";
    final String musicpath = "./res/musiccase.ser";

    public Mediacase(){
        videocase = new ArrayList<>();
        musiccase = new ArrayList<>();
        readMediacase();
    }

    public ArrayList<Media> getVideocase() {
        return videocase;
    }

    public void setVideocase(ArrayList<Media> videocase) {
        this.videocase = videocase;
    }

    public ArrayList<Media> getMusiccase() {
        return musiccase;
    }

    public void setMusiccase(ArrayList<Media> musiccase) {
        this.musiccase = musiccase;
    }

    public void addMedia(Media media, int type){
        if(type == 0)
            musiccase.add(media);
        else
            videocase.add(media);
    }

    public void removeMedia(Media media) { }

    public void writeMediacase(){
        try {
            File file = new File(videopath);
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
            FileOutputStream fileOut = new FileOutputStream(videopath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.videocase);
            out.flush();
            out.close();
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
        try {
            File file = new File(musicpath);
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
            FileOutputStream fileOut = new FileOutputStream(musicpath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.musiccase);
            out.flush();
            out.close();
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    public void readMediacase(){
        ObjectInputStream ois = null;
        try {
            FileInputStream fichier = new FileInputStream(videopath);
            ois = new ObjectInputStream(fichier);
            this.videocase = (ArrayList<Media>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            writeMediacase();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            FileInputStream fichier = new FileInputStream(musicpath);
            ois = new ObjectInputStream(fichier);
            this.musiccase = (ArrayList<Media>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            writeMediacase();
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
        this.videocase.clear();
        this.musiccase.clear();
    }
}