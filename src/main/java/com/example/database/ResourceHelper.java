package com.example.database;

import java.net.URL;
import java.util.Objects;

public class ResourceHelper {
    private ResourceHelper() {}

    private static final String BASE = "/com/example/database/";

    public static URL fxml(String fileName) {
        URL url = ResourceHelper.class.getResource(BASE + fileName);
        if (url == null) {
            throw new RuntimeException(
                    "FXML not found on classpath: " + BASE + fileName + "\n" +
                            "Make sure it exists under src/main/resources" + BASE
            );
        }
        return url;
    }

    public static URL css(String fileName) {
        URL url = ResourceHelper.class.getResource(BASE + fileName);
        if (url == null) {
            throw new RuntimeException(
                    "CSS not found on classpath: " + BASE + fileName + "\n" +
                            "Make sure it exists under src/main/resources" + BASE
            );
        }
        return url;
    }

    public static URL image(String relativePathInsideDatabaseFolder) {
        URL url = ResourceHelper.class.getResource(BASE + relativePathInsideDatabaseFolder);
        if (url == null) {
            throw new RuntimeException(
                    "Image not found on classpath: " + BASE + relativePathInsideDatabaseFolder + "\n" +
                            "Put it under src/main/resources" + BASE + relativePathInsideDatabaseFolder
            );
        }
        return url;
    }
}
