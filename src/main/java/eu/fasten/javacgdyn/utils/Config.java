package eu.fasten.javacgdyn.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {

    private static final Logger logger = Logger.getLogger(Config.class.getName());

    private Map<String, String> config;

    public Config(final String configFilePath) {
        this.config = DefaultConfig.create();

        try {
            var scanner = new Scanner(new File(configFilePath));
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    var keyValue = line.split(":", 2);
                    if (keyValue.length != 2) {
                        throw new IllegalArgumentException("Config should consist of key:value pairs " +
                                "separated by a semicolon.");
                    }
                    this.config.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (IllegalArgumentException | FileNotFoundException e) {
            logger.log(Level.WARNING, "Error while reading config: " + e.getMessage() + ". " +
                    "Using default config instead.");
            this.config = DefaultConfig.create();
        }
    }

    public String getProperty(final String key) {
        return this.config.getOrDefault(key, null);
    }
}
