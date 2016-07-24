package sample.model;

import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.utility.Utility;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Vincent on 14/06/2016.
 */
@DocumentationAnnotation(author = "Vincent Rossignol", date = "14/06/2016", description = "The Mediacase is composed with 2 ArrayList of Media : one for the video and one for the music. The mediacase contains all the medias played in our application.")
public class Mediacase {

    private ArrayList<Media> videocase;
    private ArrayList<Media> musiccase;

    public Mediacase() {
        videocase = new ArrayList<>();
        musiccase = new ArrayList<>();
        readMediacase();
    }

    public ArrayList<Media> getVideocase() {
        return videocase;
    }
    public ArrayList<Media> getMusiccase() {
        return musiccase;
    }

    public void setVideocase(ArrayList<Media> videocase) {
        this.videocase = videocase;
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

    /**
     * Write musiccase or videocase in file
     * @param path
     * @throws IOException
     */
    private void writecase(String path) throws IOException {

        File file = new File(path);

        if(!file.exists() && !file.createNewFile()) {
            return;
        }

        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        if(path.equals(Constant.PATH_TO_VIDEO)) {
            out.writeObject(this.videocase);
        } else {
            out.writeObject(this.musiccase);
        }
        out.flush();
        out.close();
        fos.close();
    }

    /**
     * Save the current mediacase in file
     */
    public void writeMediacase(){
        try {
            writecase(Constant.PATH_TO_VIDEO);
            writecase(Constant.PATH_TO_MUSIC);
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Read musiccase or videocase from file
     * @param path
     */
    private void readcase(String path) {

        FileInputStream file;
        ObjectInputStream ois = null;
        ArrayList<Media> list = new ArrayList<>();

        try {
            file = new FileInputStream(path);
            ois = new ObjectInputStream(file);
            list = (ArrayList<Media>) ois.readObject();
            Utility.checkExistingFile(list);
        } catch (FileNotFoundException e) {
            list = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            list = new ArrayList<>();
            writeMediacase();
        } finally {
            if(path.equals(Constant.PATH_TO_VIDEO)) {
                videocase = list;
            } else {
                musiccase = list;
            }
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Read previous mediacase saved before in file
     */
    public void readMediacase(){

        readcase(Constant.PATH_TO_VIDEO);
        readcase(Constant.PATH_TO_MUSIC);
    }

    public void reset(){
        this.videocase.clear();
        this.musiccase.clear();
    }
}
