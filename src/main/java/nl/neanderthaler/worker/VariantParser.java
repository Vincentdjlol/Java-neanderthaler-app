package nl.neanderthaler.worker;

import nl.neanderthaler.model.Annotation;
import nl.neanderthaler.model.Variant;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Zet VCF-dataregels om naar Variant-objecten.
 * Alleen allelen die het (eerste) sample echt draagt volgens GT worden teruggegeven.
 * Regels met GT 0/0, ./. of ALT "." leveren dus niets op.
 */
public class VariantParser {

    public List<Variant> parse(List<String> lines) {
        List<Variant> variants = new ArrayList<>();
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            try {
                variants.addAll(parseLine(line));
            } catch (IllegalArgumentException e) {
                System.err.println("Regel " + lineNumber + " overgeslagen, " + e.getMessage());
            }
        }
        return variants;
    }

    private List<Variant> parseLine(String line) {
        String[] fields = line.split("\t");
        if (fields.length < 8) {
            throw new IllegalArgumentException("minder dan 8 kolommen");
        }

        String chrom = fields[0];
        long pos = Long.parseLong(fields[1]);
        String id = fields[2];
        String ref = fields[3].toUpperCase();
        String altField = fields[4];
        double quality = fields[5].equals(".") ? Double.NaN : Double.parseDouble(fields[5]);
        String filter = fields[6];
        String info = fields[7];

        if (altField.equals(".")) {
            return List.of(); // geen alternatief allel, positie is gelijk aan de referentie
        }

        String[] alts = altField.split(",");
        Set<Integer> carried = carriedAlleles(fields, alts.length);

        List<Variant> result = new ArrayList<>();
        for (int i = 1; i <= alts.length; i++) {
            String alt = alts[i - 1].toUpperCase();
            // * = overlappende deletie, <DEL> enz. = symbolisch allel, die vergelijken we niet
            if (!carried.contains(i) || alt.equals("*") || alt.startsWith("<")) {
                continue;
            }
            result.add(new Variant(chrom, pos, id, ref, alt, quality, filter,
                    parseAnnotation(info, alt)));
        }
        return result;
    }

    /** Geeft de alt-indexen (1, 2, ...) terug die in de GT van het eerste sample staan. */
    private Set<Integer> carriedAlleles(String[] fields, int altCount) {
        Set<Integer> carried = new LinkedHashSet<>();

        // geen FORMAT/sample kolommen, dan nemen we aan dat alle ALT-allelen aanwezig zijn
        int gtIndex = fields.length >= 10 ? List.of(fields[8].split(":")).indexOf("GT") : -1;
        if (gtIndex < 0) {
            for (int i = 1; i <= altCount; i++) carried.add(i);
            return carried;
        }

        String[] sample = fields[9].split(":");
        if (gtIndex >= sample.length) {
            return carried;
        }
        for (String allele : sample[gtIndex].split("[/|]")) {
            if (allele.equals(".")) continue;
            int index = Integer.parseInt(allele);
            if (index > 0) carried.add(index);
        }
        return carried;
    }

    /** Leest het SnpEff-achtige ANN-veld (Allele|Annotation|Impact|Gene|...) voor één allel. */
    private Annotation parseAnnotation(String infoField, String alt) {
        for (String entry : infoField.split(";")) {
            if (!entry.startsWith("ANN=")) continue;
            for (String ann : entry.substring(4).split(",")) {
                String[] parts = ann.split("\\|", -1);
                if (parts.length >= 4 && parts[0].equalsIgnoreCase(alt)) {
                    String gene = parts[3].isEmpty() || parts[3].equals(".") ? null : parts[3];
                    return new Annotation(gene, parts[1], parts[2]);
                }
            }
        }
        return null;
    }
}
