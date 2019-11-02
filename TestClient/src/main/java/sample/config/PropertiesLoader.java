package sample.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PropertiesLoader {

    private final String filename;

    private PropertiesLoader(String filename) {
        this.filename = filename;
    }

    public static PropertiesLoader from(String filename) {
        return new PropertiesLoader(filename);
    }

    public Config load() throws IOException {
        try(InputStream inputStream = getClass().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + filename + "' not found in the classpath");
            }

            return new Yaml().loadAs(inputStream, Config.class);
        }
    }

}
