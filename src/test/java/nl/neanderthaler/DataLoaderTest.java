package nl.neanderthaler;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTest {

    // hardcode tot dat command line klaar is
    private final Path testFile = Path.of("test_files/test_hg38.vcf");


    // zoekt naar headers die met # beginnen en faalt dan, niet nodig voor eind versie
    @Test
    void removesHeaderLines() throws Exception {
        List<String> lines = DataLoader.load(testFile);
        for (String line : lines) {
            assertFalse(line.startsWith("#"));
        }
    }


    @Test
    void throwsWhenNoFileGiven() {
        assertThrows(IllegalArgumentException.class, () -> DataLoader.load(null));
    }

}