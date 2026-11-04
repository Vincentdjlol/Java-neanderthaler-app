package nl.neanderthaler.io;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTest {

    // Gets the small test VCF from the resources folder, it is a piece of the Neanderthaler file
    private Path testFile() throws Exception {
        return Path.of(getClass().getResource("test_files/neanderthal_example.vcf").toURI());
    }

    // The test file has 6 ## lines, 1 #CHROM line, 1 empty line and 40 data lines.
    // Only the 40 data lines should come back, without any header or empty line
    @Test
    void keepsOnlyDataLines() throws Exception {
        List<String> lines = DataLoader.load(testFile());
        assertEquals(40, lines.size());
        assertTrue(lines.stream().noneMatch(line -> line.startsWith("#") || line.isBlank()));
    }

    // loadHeader is used for the TSV output, it gives the 6 ## lines and skip the #CHROM line
    @Test
    void loadHeaderKeepsOnlyMetaLines() throws Exception {
        assertEquals(6, DataLoader.loadHeader(testFile()).size());
    }

    // Without a file the loader should stop right away with a clear error!
    @Test
    void throwsWhenNoFileGiven() {
        assertThrows(IllegalArgumentException.class, () -> DataLoader.load(null));
    }
}