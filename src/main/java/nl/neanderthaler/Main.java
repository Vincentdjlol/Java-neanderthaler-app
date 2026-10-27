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

/**
 * Starting point of the Neanderthaler DNA Comparer.
 * <p>
 * Reads both VCF files, turns them into variants, filters them and compares them.
 * Both files go through exactly the same steps, so neither side gets an advantage.
 */
@Command(name = "Java-neanderthaler-app",
        description = "Compares the variants in a human VCF with those of a Neanderthaler (both GRCh38).",
        mixinStandardHelpOptions = true) // gives us --help and --version for free
public class Main implements Runnable {

    // The only option the user really has to give right now
    @Option(
            names = {"--human"},
            description = "Path for the VCF file of your chosen human",
            required = true
    )
    private String humanvcf;

    // Falls back to the Neanderthaler file that comes with the project
    @Option(
            names = {"--neanderthaler"}, // a second human works just as well (h38)
            description = "Path for the VCF file of the neanderthaler (default ${DEFAULT-VALUE})",
            defaultValue = "test_files/neanderthaler.vcf"
    )
    private String neanderthalervcf;

    // Variants exactly on the threshold are kept, and at 0 so are variants without a quality
    @Option(
            names = {"--min-qual"},
            description = "Minimum QUAL value of a variant (default ${DEFAULT-VALUE})",
            defaultValue = "0"
    )
    private double minQuality;

    // Works with both chr21 and 21, the naming gets cleaned up later
    @Option(
            names = {"--chrom"},
            description = "Only compare this chromosome, for example chr21 or 21"
    )
    private String chromosome;

    // Only used when the user wants the results in a file as well
    @Option(
            names = {"--output"},
            description = "Write all variants with their category to this TSV file"
    )
    private Path output;

    // Without this flag only the summary is printed, the separate variants stay out of the terminal
    @Option(
            names = {"--details"},
            description = "Also show the separate variants in the terminal"
    )
    private boolean details;

    /**
     * Runs the whole comparison. Picocli only gets here once every option checks out.
     */
    @Override
    public void run() {
        System.out.println("Human VCF: " + humanvcf);
        System.out.println("Neanderthaler VCF: " + neanderthalervcf);

        try {
            List<String> humanLines = DataLoader.load(Path.of(humanvcf));
            List<String> neanderthalerLines = DataLoader.load(Path.of(neanderthalervcf));

            // Only alleles the person actually carries make it through, so a 0/0 position just disappears
            VariantParser parser = new VariantParser();
            List<Variant> humanVariants = parser.parse(humanLines);
            List<Variant> neanderthalerVariants = parser.parse(neanderthalerLines);

            // Same filter for both, otherwise the shared percentage would be unfair
            VariantFilter filter = new VariantFilter(minQuality, chromosome);
            List<Variant> humanFiltered = filter.filter(humanVariants);
            List<Variant> neanderthalerFiltered = filter.filter(neanderthalerVariants);

            // A typo in --chrom gives an empty list, so tell the user instead of printing a report full of zeros
            if (chromosome != null && humanFiltered.isEmpty()) {
                System.err.println("No human variants on chromosome " + chromosome + ". Does this chromosome exist?");
                return;
            }
            if (chromosome != null && neanderthalerFiltered.isEmpty()) {
                System.err.println("No Neanderthaler variants on chromosome " + chromosome + ".");
            }

            VariantComparator comparator = new VariantComparator();
            ComparisonResult result = comparator.compare(humanFiltered, neanderthalerFiltered);

            OutputWriter writer = new OutputWriter();
            writer.write(result, details);
            if (output != null) {
                // The header of the human VCF goes on top, so you can always see where the results came from
                writer.writeTsv(result, output, DataLoader.loadHeader(Path.of(humanvcf)));
            }
        } catch (IOException e) { // reading a VCF or writing the TSV can both end up here
            System.err.println("Could not read or write file " + e.getMessage());
        } catch (IllegalArgumentException e) { // for example a file that does not exist
            System.err.println("Error " + e.getMessage());
        }
    }

    /**
     * Passes the arguments to picocli, which checks them and then starts the run method.
     *
     * @param args the options the user typed after the jar
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}