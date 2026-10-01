package nl.neanderthaler;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// info over het programma die picocli toont bij --help
@Command(name = "Java-neanderthaler-app",
        description = "Test description want ik heb geen zin in een echte te bedenken nu!",
        mixinStandardHelpOptions = true) // voegt --help en --version toe

public class Main implements Runnable {

    @Option(
            names = {"--human"},
            description = "Path voor VCF file van jouw gekozen mens",
            required = true
    )
    private String humanvcf;


    @Option(
            names = {"--neanderthaler"}, // of 2e mens
            description = "Path voor VCF file van de Neanderthaler (voorbeeld path)",
            required = true
    )
    private String neanderthalervcf;

    // dit wordt door picocli aangeroepen nadat de opties zijn ingevuld
    @Override
    public void run() {
        System.out.println("Mens VCF: " + humanvcf);
        System.out.println("Neanderthaler VCF: " + neanderthalervcf);

        try {

            List<String> humanLines = DataLoader.load(Path.of(humanvcf)); // roept DataLoader aan en leest bestanden dan in
            List<String> neanderthalerLines = DataLoader.load(Path.of(neanderthalervcf));

            // regels omzetten naar Variant-objecten
            VariantParser parser = new VariantParser();
            List<Variant> humanVariants = parser.parse(humanLines);
            List<Variant> neanderthalerVariants = parser.parse(neanderthalerLines);

            // varianten filteren
            VariantFilter filter = new VariantFilter(0.0, null); // 0.0 en null zijn tijdelijke waarden
            List<Variant> humanFiltered = filter.filter(humanVariants);
            List<Variant> neanderthalerFiltered = filter.filter(neanderthalerVariants);

            // mens en Neanderthaler vergelijken
            VariantComparator comparator = new VariantComparator();
            ComparisonResult result = comparator.compare(humanFiltered, neanderthalerFiltered);

            OutputWriter writer = new OutputWriter();
            writer.write(result); // result!!
        } catch (IOException e) { // als bestand niet gelezen kan worden
            System.err.println("Could not read VCF file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args); // geeft de argumenten door aan picocli
        System.exit(exitCode);
    }
}