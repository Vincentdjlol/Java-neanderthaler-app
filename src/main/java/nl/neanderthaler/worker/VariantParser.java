package nl.neanderthaler.worker;

import nl.neanderthaler.model.Annotation;
import nl.neanderthaler.model.Variant;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Turns VCF data lines into Variant objects.
 * Only alleles that the (first) sample really carries according to GT are returned.
 * Lines with GT 0/0, ./. or ALT "." therefore give nothing back.
 */
public class VariantParser {

    /**
     * Parses all lines. A broken line is reported and skipped, so one bad record does not stop the whole file.
     *
     * @param lines the data lines from the DataLoader
     * @return every carried allele as its own Variant
     */
    public List<Variant> parse(List<String> lines) {
        List<Variant> variants = new ArrayList<>();
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            try {
                variants.addAll(parseLine(line));
            } catch (IllegalArgumentException e) {
                System.err.println("Line " + lineNumber + " skipped, " + e.getMessage());
            }
        }
        return variants;
    }

    private List<Variant> parseLine(String line) {
        String[] fields = line.split("\t");
        if (fields.length < 8) {
            throw new IllegalArgumentException("fewer than 8 columns");
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
            return List.of(); // no alternative allele, this position is the same as the reference
        }

        String[] alts = altField.split(",");
        Set<Integer> carried = carriedAlleles(fields, alts.length);

        List<Variant> result = new ArrayList<>();
        for (int i = 1; i <= alts.length; i++) {
            String alt = alts[i - 1].toUpperCase();
            // A star is an overlapping deletion and <DEL> and friends are symbolic alleles, we do not compare those
            if (!carried.contains(i) || alt.equals("*") || alt.startsWith("<")) {
                continue;
            }
            result.add(new Variant(chrom, pos, id, ref, alt, quality, filter,
                    parseAnnotation(info, alt)));
        }
        return result;
    }

    /** Returns the ALT numbers (1, 2, ...) that appear in the GT of the first sample. */
    private Set<Integer> carriedAlleles(String[] fields, int altCount) {
        Set<Integer> carried = new LinkedHashSet<>();

        // Without FORMAT and sample columns we assume every ALT allele is present
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

    /** Reads the SnpEff style ANN field (Allele|Annotation|Impact|Gene|...) for one allele. */
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