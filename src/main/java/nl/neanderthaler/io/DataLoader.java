/* In deze  file VCF bestanden inlezen */
package nl.neanderthaler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class DataLoader {

    public static List<String> load(Path path) throws IOException { // geeft leesfout door aan aanroeper (throw IOException)
        if (path == null) { // geen pad
            throw new IllegalArgumentException("No file given");
        }
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + path);
        }
        return Files.readAllLines(path).stream()
                .filter(line -> !line.startsWith("#"))
                .filter(line -> !line.isBlank())
                .toList();
    }
}