import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Help;

@Command(name = "Java-neanderthaler-app",
        description = "Test description want ik heb geen zin in een echte te bedenken nu!",
        mixinStandardHelpOptions = true)

public class Main implements Runnable {

    @Option(
            names = {"--human"},
            description = "Path voor VCF file van jouw gekozen mens",
            required = true
    )
    private String humanvcf;

    @Option(
            names = {"--neanderthaler"},
            description = "Path voor VCF file van de Neanderthaler (voorbeeld path)",
            required = true
    )
    private String neanderthalervcf;

    @Override
    public void run() {
        System.out.println("Mens VCF: " + humanvcf);
        System.out.println("Neanderthaler VCF: " + neanderthalervcf);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
