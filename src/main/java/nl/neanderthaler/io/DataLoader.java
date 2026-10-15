// In deze file VCF-bestanden inlezen
package nl.neanderthaler.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class DataLoader {

    public static List<String> load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("No file given");
        }
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist:" + path);
        }

        List<String> lines = Files.readAllLines(path).stream()
                .filter(line -> !line.startsWith("#"))
                .filter(line -> !line.isBlank())
                .toList();

        System.out.println("Er zijn " + lines.size() + " varianten in " + path);

        return lines;
    }

}