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

    private static final int MAX_LISTED = 20; // zoveel varianten per lijst op het scherm

    /** Schrijft een leesbaar rapport naar de console. */
    public void write(ComparisonResult result) {
        System.out.println();
        System.out.println(formatSummary(result));
        System.out.println(formatPerChromosome(result));
        System.out.println(formatPerType(result));

        printList("Gedeelde varianten", result.getShared());
        printList("Zelfde positie, ander allel dan de Neanderthaler", result.getDifferentAllele());
        printList("Alleen in mens met HIGH of MODERATE impact", highImpact(result.getOnlyInHuman()));

        System.out.println("Let op. Het percentage is het aandeel humane varianten dat de Neanderthaler ook draagt.");
        System.out.println("Het is geen maat voor het percentage Neanderthaler-afkomst.");
    }

    /** Schrijft alle varianten met hun categorie als TSV, handig voor grote bestanden. */
    public void writeTsv(ComparisonResult result, Path path) throws IOException {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path))) {
            out.println("category\tchrom\tpos\tref\talt\ttype\tgene\teffect\timpact");
            writeRows(out, "shared", result.getShared());
            writeRows(out, "only_human", result.getOnlyInHuman());
            writeRows(out, "only_neanderthaler", result.getOnlyInNeanderthaler());
        }
        System.out.println("Volledige lijst geschreven naar " + path);
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
        sb.append("=== Vergelijking mens vs Neanderthaler ===\n");
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Varianten in mens (na filter)", result.getHumanTotal()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Varianten in Neanderthaler (na filter)", result.getNeanderthalerTotal()));
        sb.append(String.format(Locale.ROOT, "%-40s %d (%.1f%% van de humane varianten)%n",
                "Gedeeld", result.getSharedCount(), result.getSharedPercentage()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Alleen in mens", result.getOnlyInHuman().size()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "  waarvan ander allel op zelfde positie", result.getDifferentAllele().size()));
        sb.append(String.format(Locale.ROOT, "%-40s %d%n", "Alleen in Neanderthaler", result.getOnlyInNeanderthaler().size()));
        return sb.toString();
    }

    private String formatPerChromosome(ComparisonResult result) {
        // per chromosoom {mens, neanderthaler, gedeeld}
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

        StringBuilder sb = new StringBuilder("Per chromosoom\n");
        sb.append(String.format(Locale.ROOT, "  %-6s %8s %8s %8s %9s%n", "chrom", "mens", "neand", "gedeeld", "% gedeeld"));
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

        StringBuilder sb = new StringBuilder("Per varianttype (humane varianten)\n");
        sb.append(String.format(Locale.ROOT, "  %-10s %8s %8s%n", "type", "mens", "gedeeld"));
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
            System.out.println("  ... en nog " + (variants.size() - MAX_LISTED) + ", gebruik --output voor de volledige lijst");
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

    /** Sorteert 1, 2, ..., 22, X, Y, MT in plaats van 1, 10, 11, ... */
    private static int compareChromosomes(String a, String b) {
        boolean aNum = a.matches("\\d+");
        boolean bNum = b.matches("\\d+");
        if (aNum && bNum) return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
        if (aNum) return -1;
        if (bNum) return 1;
        return a.compareTo(b);
    }
}
