package nl.neanderthaler.io;

import nl.neanderthaler.model.Annotation;
import nl.neanderthaler.model.ComparisonResult;
import nl.neanderthaler.model.Variant;
import nl.neanderthaler.model.VariantType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class OutputWriter {

    private static final int MAX_LISTED = 20; // how many variants per list are shown on screen

    /**
     * Prints the report to the console. The separate variants are only shown when the user asks for them.
     *
     * @param result  the outcome of the comparison
     * @param details true when --details was given
     */
    public void write(ComparisonResult result, boolean details) {
        System.out.println();
        System.out.println(formatSummary(result));
        System.out.println(formatPerChromosome(result));
        System.out.println(formatPerType(result));

        if (details) {
            printList("Shared variants", result.getShared());
            printList("Same position, different allele than the Neanderthaler", result.getDifferentAllele());
            printList("Only in human with HIGH or MODERATE impact", highImpact(result.getOnlyInHuman()));
        } else {
            System.out.println("Use --details or --output to see the separate variants.");
        }

        System.out.println("Note. The percentage is the share of human variants that the Neanderthaler also carries.");
        System.out.println("It is not a measure of Neanderthal ancestry.");
    }

    /**
     * Writes every variant with its category as TSV, with the header of the input VCF on top.
     *
     * @param result the outcome of the comparison
     * @param path   the file to write to
     * @param header the ## lines copied from the human VCF
     */
    public void writeTsv(ComparisonResult result, Path path, List<String> header) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            // Our own line first, then the copied VCF header, then the column names
            out.println("##source=Java-neanderthaler-app");
            for (String line : header) {
                out.println(line);
            }
            out.println("#category\tchrom\tpos\tref\talt\ttype\tgene\teffect\timpact");
            writeRows(out, "shared", result.getShared());
            writeRows(out, "only_human", result.getOnlyInHuman());
            writeRows(out, "only_neanderthaler", result.getOnlyInNeanderthaler());
        }
        System.out.println("Full list written to " + path);
    }

    private void writeRows(PrintWriter out, String category, List<Variant> variants) {
        for (Variant v : variants) {
            Annotation a = v.getAnnotation();
            out.println(String.join("\t", category, v.getChromosome(), String.valueOf(v.getPosition()),
                    v.getRef(), v.getAlt(), v.getType().name(),
                    a == null || a.getGene() == null ? "." : a.getGene(),
                    a == null ? "." : a.getEffect(),
                    a == null ? "." : a.getImpact()));
        }
    }

    private String formatSummary(ComparisonResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Comparison human vs Neanderthaler ===\n");
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Variants in human (after filter)", result.getHumanTotal()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Variants in Neanderthaler (after filter)", result.getNeanderthalerTotal()));
        sb.append(String.format(Locale.ROOT, "%-40s %d (%.1f%% of the human variants)%n",
                "Shared", result.getSharedCount(), result.getSharedPercentage()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Only in human", result.getOnlyInHuman().size()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "  of which different allele at same position", result.getDifferentAllele().size()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Only in Neanderthaler", result.getOnlyInNeanderthaler().size()));
        return sb.toString();
    }

    private String formatPerChromosome(ComparisonResult result) {
        // per chromosome {human, neanderthaler, shared}
        Map<String, int[]> counts = new TreeMap<>(OutputWriter::compareChromosomes);
        for (Variant v : result.getShared()) {
            int[] c = counts.computeIfAbsent(v.getNormalizedChromosome(), k -> new int[3]);
            c[0]++; c[1]++; c[2]++;
        }
        for (Variant v : result.getOnlyInHuman()) {
            counts.computeIfAbsent(v.getNormalizedChromosome(), k -> new int[3])[0]++;
        }
        for (Variant v : result.getOnlyInNeanderthaler()) {
            counts.computeIfAbsent(v.getNormalizedChromosome(), k -> new int[3])[1]++;
        }

        StringBuilder sb = new StringBuilder("Per chromosome\n");
        sb.append(String.format(Locale.ROOT, "  %-6s %8s %8s %8s %9s%n", "chrom", "human", "neand", "shared", "% shared"));
        for (Map.Entry<String, int[]> e : counts.entrySet()) {
            int[] c = e.getValue();
            double pct = c[0] == 0 ? 0.0 : 100.0 * c[2] / c[0];
            sb.append(String.format(Locale.ROOT, "  %-6s %8d %8d %8d %8.1f%%%n", e.getKey(), c[0], c[1], c[2], pct));
        }
        return sb.toString();
    }

    private String formatPerType(ComparisonResult result) {
        Map<VariantType, int[]> counts = new EnumMap<>(VariantType.class);
        for (Variant v : result.getShared()) {
            int[] c = counts.computeIfAbsent(v.getType(), k -> new int[2]);
            c[0]++; c[1]++;
        }
        for (Variant v : result.getOnlyInHuman()) {
            counts.computeIfAbsent(v.getType(), k -> new int[2])[0]++;
        }

        StringBuilder sb = new StringBuilder("Per variant type (human variants)\n");
        sb.append(String.format(Locale.ROOT, "  %-10s %8s %8s%n", "type", "human", "shared"));
        for (Map.Entry<VariantType, int[]> e : counts.entrySet()) {
            sb.append(String.format(Locale.ROOT, "  %-10s %8d %8d%n", e.getKey(), e.getValue()[0], e.getValue()[1]));
        }
        return sb.toString();
    }

    private String formatVariant(Variant variant) {
        Annotation a = variant.getAnnotation();
        String annotation = a == null ? "" : String.format("%-8s %-22s %s",
                a.getGene() == null ? "-" : a.getGene(), a.getEffect(), a.getImpact());
        return String.format(Locale.ROOT, "  %-18s %-10s %-9s %s",
                variant.getChromosome() + ":" + variant.getPosition(),
                variant.getRef() + ">" + variant.getAlt(),
                variant.getType(), annotation);
    }

    private void printList(String title, List<Variant> variants) {
        System.out.println(title + " (" + variants.size() + ")");
        variants.stream().limit(MAX_LISTED).map(this::formatVariant).forEach(System.out::println);
        if (variants.size() > MAX_LISTED) {
            System.out.println("  ... and " + (variants.size() - MAX_LISTED) + " more, use --output for the full list");
        }
        System.out.println();
    }

    private List<Variant> highImpact(List<Variant> variants) {
        List<Variant> result = new ArrayList<>();
        for (Variant v : variants) {
            Annotation a = v.getAnnotation();
            if (a != null && ("HIGH".equals(a.getImpact()) || "MODERATE".equals(a.getImpact()))) {
                result.add(v);
            }
        }
        result.sort(Comparator.comparing(v -> "HIGH".equals(v.getAnnotation().getImpact()) ? 0 : 1));
        return result;
    }

    /** Natural chromosome order, so chr2 is listed before chr10 instead of after it. */
    private static int compareChromosomes(String a, String b) {
        boolean aNum = a.matches("\\d+");
        boolean bNum = b.matches("\\d+");
        if (aNum && bNum) return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
        if (aNum) return -1;
        if (bNum) return 1;
        return a.compareTo(b);
    }
}