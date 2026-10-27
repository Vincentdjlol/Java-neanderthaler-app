// Reads VCF files and hands the data lines to the rest of the program
package nl.neanderthaler.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class DataLoader {

    /**
     * Reads a VCF and keeps only the data lines, so headers and empty lines are dropped.
     *
     * @param path the VCF file to read
     * @return one string per data line
     * @throws IOException when the file cannot be read
     */
    public static List<String> load(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("No file given");
        }
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist " + path);
        }

        List<String> lines = Files.readAllLines(path).stream()
                .filter(line -> !line.startsWith("#")) // removes the header lines
                .filter(line -> !line.isBlank()) // removes empty lines
                .toList();

        System.out.println("Read " + lines.size() + " data lines from " + path);

        return lines;
    }

    /**
     * Reads only the ## meta lines of a VCF, so they can be copied into the output file.
     *
     * @param path the VCF file to read the header from
     * @return all lines that start with ##
     * @throws IOException when the file cannot be read
     */
    public static List<String> loadHeader(Path path) throws IOException {
        return Files.readAllLines(path).stream()
                .filter(line -> line.startsWith("##"))
                .toList();
    }

}