import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by thomasfouan on 09/03/2016.
 */
public class StreamMedia {

    private final MediaPlayerFactory factory;

    private final MediaListPlayer mediaListPlayer;

    private final MediaList playList;

    private final int PORT = 2016;

    public StreamMedia(String addr, int port) {
        factory = new MediaPlayerFactory();
        mediaListPlayer = factory.newMediaListPlayer();
        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
            @Override
            public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
                System.out.println("Playing next item: " + itemMrl + " (" + item + ")");
            }
        });
        playList = factory.newMediaList();
        String rtspStream = formatRtspStream(addr, PORT, "demo");
        System.out.println("Prepare for streaming at : "+rtspStream);
        playList.setStandardMediaOptions(rtspStream,
                ":no-sout-rtp-sap",
                ":no-sout-standard-sap",
                ":sout-all",
                ":sout-keep");
        mediaListPlayer.setMediaList(playList);
    }

    public MediaPlayerFactory getFactory() {
        return factory;
    }

    public MediaListPlayer getMediaListPlayer() {
        return mediaListPlayer;
    }

    public MediaList getPlayList() {
        return playList;
    }

    public void start(String dir, String address, int port) throws Exception {
        System.out.println("Scanning for audio files...");
        // Scan for media files
        List<File> files = scanForMedia(dir);
        // Randomise the order
        Collections.shuffle(files);
        // Prepare the media options for streaming
        String mediaOptions = formatRtpStream(address, port);
        // Add each media file to the play-list...
        for(File file : files) {
            // You could instead set standard options on the media list player rather
            // than setting options each time you add media
            playList.addMedia(file.getAbsolutePath(), mediaOptions);
        }
        // Loop the play-list over and over
        mediaListPlayer.setMode(MediaListPlayerMode.LOOP);
        // Attach the play-list to the media list player
        mediaListPlayer.setMediaList(playList);
        // Finally, start the media player
        mediaListPlayer.play();
        System.out.println("Streaming started at rtp://" + address + ":" + port);
        // Wait forever...
        Thread.currentThread().join();
    }

    /**
     * Search a directory, recursively, for mp3 files.
     *
     * @param root root directory
     * @return collection of mp3 files
     */
    private List<File> scanForMedia(String root) {
        List<File> result = new ArrayList<File>(400);
        scanForMedia(new File(root), result);
        return result;
    }

    private void scanForMedia(File root, List<File> result) {
        if(root.exists() && root.isDirectory()) {
            // List all matching mp3 files...
            File[] files = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".mp3");
                }
            });
            // Add them to the collection
            result.addAll(Arrays.asList(files));
            // List all nested directories...
            File[] dirs = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            // Recursively scan each nested directory...
            for(File dir : dirs) {
                scanForMedia(dir, result);
            }
        }
    }

    private String formatRtpStream(String serverAddress, int serverPort) {
        StringBuilder sb = new StringBuilder(60);
        sb.append(":sout=#rtp{dst=");
        sb.append(serverAddress);
        sb.append(",port=");
        sb.append(serverPort);
        sb.append(",mux=ts}");
        return sb.toString();
    }

    private String formatRtspStream(String serverAddress, int serverPort, String id) {
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
}
