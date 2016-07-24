package sample.utility;

import sample.annotation.DocumentationAnnotation;
import sample.constant.Constant;
import sample.model.Media;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 23/07/2016.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date = "23/07/2016", description = "This is an Utility Class available for all class to use same methods.")
public class Utility {

    /**
     * Write Data as an InputStream in the file represented by the path given in parameter
     * @param is
     * @param path
     * @throws IOException
     */
    public static void writeInFile(InputStream is, String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(path));
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;

        if (is != null) {
            while((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        fos.close();
    }

    /**
     * Represents a time in milliseconds into a String formatted as "HH:mm:ss"
     * @param time
     * @return String
     */
    public static String formatTime(long time) {

        int hours, minutes;

        // milliseconds to seconds
        time /= 1000;
        hours = (int) time/3600;
        time -= hours*3600;
        minutes = (int) time/60;
        time -= minutes*60;

        return String.format("%02d:%02d:%02d", hours, minutes, time);
    }

    /**
     * Return a string representing a RTSP stream URL for the given address, port and id.
     * @param serverAddress
     * @param serverPort
     * @param id
     * @return String
     */
    public static String formatRtspStream(String serverAddress, int serverPort, String id) {
        StringBuilder sb = new StringBuilder(60);
        sb.append(":sout=#rtp{sdp=rtsp://@");
        sb.append(serverAddress);
        sb.append(':');
        sb.append(serverPort);
        sb.append('/');
        sb.append(id);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Get file extension
     * @param fileName
     * @return extension
     */
    public static String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    /**
     * Check if the audio extension given in parameter is supported by the player
     * @param extension
     * @return True if it is supported. Otherwise, return false
     */
    public static boolean audioExtensionIsSupported(String extension){
        for(String str: Constant.EXTENSIONS_AUDIO){
            if(extension.toLowerCase().compareTo(str.toLowerCase()) == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the video extension given in parameter is supported by the player
     * @param extension
     * @return
     */
    public static boolean videoExtensionIsSupported(String extension) {
        for (String str : Constant.EXTENSIONS_VIDEO) {
            if (extension.toLowerCase().compareTo(str.toLowerCase()) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if each media in the list still exists
     * @param list
     */
    public static void checkExistingFile(List<Media> list) {
        List<Media> found = new ArrayList<>();
        for(Media media : list) {
            if(!Files.exists(Paths.get(media.getPath()))) {
                found.add(media);
            }
        }
        list.removeAll(found);
    }
}
