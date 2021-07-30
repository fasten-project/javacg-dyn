package eu.fasten.javacgdyn.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MavenUtils {

    public static String extractMavenCoordinate(Path root) {
        var files = root.toFile().listFiles();
        if (files != null && Arrays.stream(files).anyMatch(f -> f.getName().equals("pom.xml"))) {
            return extractMavenCoordinateFromPom(Path.of(root.toString(), "pom.xml"));
        } else {
            return null;
        }
    }

    private static String extractMavenCoordinateFromPom(Path pomPath) {
        try {
            var pomContent = Files.readString(pomPath);
            var groupId = substringBetween(pomContent, "<groupId>", "</groupId>", 0);
            var start = 0;
            var matcher = Pattern.compile("<parent>(.|\\n|\\r|\\t)*</parent>").matcher(pomContent);
            if (matcher.find()) {
                start = matcher.end();
            }
            var artifactId = substringBetween(pomContent, "<artifactId>", "</artifactId>", start);
            var version = substringBetween(pomContent, "<version>", "</version>", 0);
            return groupId + ":" + artifactId + ":" + version;
        } catch (IOException e) {
            System.err.println("Error extracting Maven coordinate from POM file: " + e.getMessage());
        }
        return null;
    }

    private static String substringBetween(String str, String before, String after, int start) {
        return str.substring(str.indexOf(before, start) + before.length(), str.indexOf(after, start));
    }
}
