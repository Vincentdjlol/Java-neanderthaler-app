package nl.neanderthaler.model;

import java.util.Objects;

/**
 * One ALT allele at one position that the individual really carries (GT is not 0/0).
 * A multi-allelic VCF line (ALT = C,G) is split by the parser
 * into several Variant objects.
 */
public class Variant {

    private final String chromosome;
    private final long position;
    private final String id;
    private final String ref;
    private final String alt;
    private final double quality;   // Double.NaN when QUAL is a dot
    private final String filter;
    private final Annotation annotation;

    public Variant(String chromosome, long position, String id, String ref, String alt,
                   double quality, String filter, Annotation annotation) {
        this.chromosome = chromosome;
        this.position = position;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.quality = quality;
        this.filter = filter;
        this.annotation = annotation;
    }

    public String getChromosome() { return chromosome; }
    public long getPosition() { return position; }
    public String getId() { return id; }
    public String getRef() { return ref; }
    public String getAlt() { return alt; }
    public double getQuality() { return quality; }
    public String getFilter() { return filter; }
    public Annotation getAnnotation() { return annotation; }

    /** chr1, Chr1 and 1 all become 1, so files with different naming still match. */
    public static String normalizeChromosome(String chrom) {
        if (chrom == null) {
            return null;
        }
        String c = chrom.trim();
        if (c.toLowerCase().startsWith("chr")) {
            c = c.substring(3);
        }
        if (c.equalsIgnoreCase("M")) {
            c = "MT";
        }
        return c.toUpperCase();
    }

    public String getNormalizedChromosome() {
        return normalizeChromosome(chromosome);
    }

    public VariantType getType() {
        if (ref.length() == 1 && alt.length() == 1) {
            return VariantType.SNP;
        }
        if (alt.length() > ref.length()) {
            return VariantType.INSERTION;
        }
        if (alt.length() < ref.length()) {
            return VariantType.DELETION;
        }
        return VariantType.OTHER; // for example an MNP such as AT>GC
    }

    /** Position key without the allele, used to find "same position, different allele". */
    public String getPositionKey() {
        return getNormalizedChromosome() + ":" + position;
    }

    /** Unique key built from chromosome, position, REF and ALT. */
    public String getKey() {
        return getPositionKey() + ":" + ref + ">" + alt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variant other)) return false;
        return getKey().equals(other.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    public String toString() {
        return chromosome + ":" + position + " " + ref + ">" + alt;
    }
}