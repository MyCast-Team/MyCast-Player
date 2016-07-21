package sample.constant;

import sample.annotation.DocumentationAnnotation;

/**
 * Created by thomasfouan on 20/07/2016.
 */
@DocumentationAnnotation(author = "Thomas Fouan", date="20/07/2016", description = "This class contains all our constants. For example, there are paths to resource files or even format filters.")
public class Constant {

    /**
     * Define the path of the main view of the app
     */
    public static final String pathToMainView = "/sample/view/mainFrame.fxml";

    /**
     * Define the path where all the plugins are stored
     */
    public static final String pathToPlugin = "plugin";

    /**
     * Define the path to the resource directory
     */
    public static final String pathToResources = "./res";

    /**
     * Define the path where the plugin file to communicate with the server is stored
     */
    public static final String pathToPluginFile = "./res/plugin.json";

    /**
     * Define the path where the interface configuration is stored
     */
    public static final String pathToInterfaceConf = "./res/interface.csv";

    /**
     * Define the path where the Id of the user is stored
     */
    public static final String pathToId = "./res/id.json";

    /**
     * Define the path where the mediacase file to communicate with the server is stored
     */
    public static final String pathToMediacase = "./res/mediacase.json";

    /**
     * Define the path where the playlist is stored
     */
    public static final String pathToPlaylist = "./res/playlist.ser";

    /**
     * Define the path where the video library is stored
     */
    public static final String pathToVideo = "./res/videocase.ser";

    /**
     * Define the path where the music library is stored
     */
    public static final String pathToMusic = "./res/musiccase.ser";

    /**
     * Define the package name where the main view of a plugin is stored
     */
    public static final String packageName = "plugin";

    /**
     * Define all default interfaces
     */
    public static final String[] staticInterfaces = {"/sample/view/suggestions.fxml", "/sample/view/playlist.fxml", "/sample/view/player.fxml", "/sample/view/plugin.fxml", "/sample/view/mediacase.fxml"};

    /**
     * Define the port number for streaming media between the main application and the client application
     */
    public static final int PORT = 5555;

    /**
     * Define all audio extensions that can be used with the player
     */
    public static final String[] EXTENSIONS_AUDIO = {
            "3ga",
            "669",
            "a52",
            "aac",
            "ac3",
            "adt",
            "adts",
            "aif",
            "aifc",
            "aiff",
            "amb",
            "amr",
            "aob",
            "ape",
            "au",
            "awb",
            "caf",
            "dts",
            "flac",
            "it",
            "kar",
            "m4a",
            "m4b",
            "m4p",
            "m5p",
            "mid",
            "mka",
            "mlp",
            "mod",
            "mpa",
            "mp1",
            "mp2",
            "mp3",
            "mpc",
            "mpga",
            "mus",
            "oga",
            "ogg",
            "oma",
            "opus",
            "qcp",
            "ra",
            "rmi",
            "s3m",
            "sid",
            "spx",
            "tak",
            "thd",
            "tta",
            "voc",
            "vqf",
            "w64",
            "wav",
            "wma",
            "wv",
            "xa",
            "xm"
    };

    /**
     * Define all video extensions that can be used with the player
     */
    public static final String[] EXTENSIONS_VIDEO = {
            "3g2",
            "3gp",
            "3gp2",
            "3gpp",
            "amv",
            "asf",
            "avi",
            "bik",
            "bin",
            "divx",
            "drc",
            "dv",
            "evo",
            "f4v",
            "flv",
            "gvi",
            "gxf",
            "iso",
            "m1v",
            "m2v",
            "m2t",
            "m2ts",
            "m4v",
            "mkv",
            "mov",
            "mp2",
            "mp2v",
            "mp4",
            "mp4v",
            "mpe",
            "mpeg",
            "mpeg1",
            "mpeg2",
            "mpeg4",
            "mpg",
            "mpv2",
            "mts",
            "mtv",
            "mxf",
            "mxg",
            "nsv",
            "nuv",
            "ogg",
            "ogm",
            "ogv",
            "ogx",
            "ps",
            "rec",
            "rm",
            "rmvb",
            "rpl",
            "thp",
            "tod",
            "ts",
            "tts",
            "txd",
            "vob",
            "vro",
            "webm",
            "wm",
            "wmv",
            "wtv",
            "xesc"
    };
}

