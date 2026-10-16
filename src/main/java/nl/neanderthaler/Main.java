package nl.neanderthaler;

import nl.neanderthaler.io.DataLoader;
import nl.neanderthaler.io.OutputWriter;
import nl.neanderthaler.model.Variant;
import nl.neanderthaler.model.ComparisonResult;
import nl.neanderthaler.worker.VariantComparator;
import nl.neanderthaler.worker.VariantFilter;
import nl.neanderthaler.worker.VariantParser;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// info over het programma die picocli toont bij --help
@Command(name = "Java-neanderthaler-app",
        description = "Vergelijkt de varianten in een humane VCF met die van een Neanderthaler (beide GRCh38).",
        mixinStandardHelpOptions = true) // voegt --help en --version toe

public class Main implements Runnable {

    @Option(
            names = {"--human"},
            description = "Path for the VCF file of your chosen human",
            required = true
    )
    private String humanvcf;

    @Option(
            names = {"--neanderthaler"}, // of 2e mens
            description = "Path for the VCF file of the neanderthaler (default ${DEFAULT-VALUE})",
            defaultValue = "test_files/neanderthaler.vcf"
    )
    private String neanderthalervcf;

    @Option(
            names = {"--min-qual"},
            description = "Minimale QUAL-waarde van een variant (default ${DEFAULT-VALUE})",
            defaultValue = "0"
    )
    private double minQuality;

    @Option(
            names = {"--chrom"},
            description = "Alleen dit chromosoom vergelijken, bijvoorbeeld chr21 of 21"
    )
    private String chromosome;

    @Option(
            names = {"--output"},
            description = "Schrijf alle varianten met hun categorie naar dit TSV-bestand"
    )
    private Path output;

    // dit wordt door picocli aangeroepen nadat de opties zijn ingevuld
    @Override
    public void run() {
        System.out.println("Human VCF: " + humanvcf);
        System.out.println("Neanderthaler VCF: " + neanderthalervcf);

        try {
            List<String> humanLines = DataLoader.load(Path.of(humanvcf)); // roept DataLoader aan en leest bestanden dan in
            List<String> neanderthalerLines = DataLoader.load(Path.of(neanderthalervcf));

            // regels omzetten naar Variant-objecten (alleen allelen die het individu echt draagt)
            VariantParser parser = new VariantParser();
            List<Variant> humanVariants = parser.parse(humanLines);
            List<Variant> neanderthalerVariants = parser.parse(neanderthalerLines);

            // varianten filteren op FILTER-kolom, kwaliteit en eventueel chromosoom
            VariantFilter filter = new VariantFilter(minQuality, chromosome);
            List<Variant> humanFiltered = filter.filter(humanVariants);
            List<Variant> neanderthalerFiltered = filter.filter(neanderthalerVariants);

            // mens en Neanderthaler vergelijken
            VariantComparator comparator = new VariantComparator();
            ComparisonResult result = comparator.compare(humanFiltered, neanderthalerFiltered);

            OutputWriter writer = new OutputWriter();
            writer.write(result);
            if (output != null) {
                writer.writeTsv(result, output);
            }
        } catch (IOException e) { // als bestand niet gelezen kan worden
            System.err.println("Could not read VCF file: " + e.getMessage());
        } catch (IllegalArgumentException e) { // bijvoorbeeld als bestand niet bestaat
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args); // geeft de argumenten door aan picocli
        System.exit(exitCode);
    }
}
