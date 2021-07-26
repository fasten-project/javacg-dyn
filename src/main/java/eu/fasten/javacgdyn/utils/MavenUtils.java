package eu.fasten.javacgdyn.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

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
            var artifactId = substringBetween(pomContent, "<artifactId>", "</artifactId>");
            var groupId = substringBetween(pomContent, "<groupId>", "</groupId>");
            var version = substringBetween(pomContent, "<version>", "</version>");
            return groupId + ":" + artifactId + ":" + version;
        } catch (IOException e) {
            System.err.println("Error extracting Maven coordinate from POM file: " + e.getMessage());
        }
        return null;
    }

    private static String substringBetween(String str, String before, String after) {
        return str.substring(str.indexOf(before) + before.length(), str.indexOf(after));
    }
}
