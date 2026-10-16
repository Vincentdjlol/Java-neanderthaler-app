package nl.neanderthaler.model;

import java.util.Objects;

/**
 * Eén ALT-allel op één positie dat het individu echt draagt (GT niet 0/0).
 * Een multi-allelische VCF-regel (ALT = C,G) wordt door de parser opgesplitst
 * in meerdere Variant-objecten.
 */
public class Variant {

    private final String chromosome;
    private final long position;
    private final String id;
    private final String ref;
    private final String alt;
    private final double quality;   // Double.NaN als QUAL een punt is
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

    /** chr1, Chr1 en 1 worden allemaal 1, zodat bestanden met verschillende naamgeving matchen. */
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
        return VariantType.OTHER; // bijvoorbeeld een MNP zoals AT>GC
    }

    /** Positiesleutel zonder allel, om "zelfde positie, ander allel" te vinden. */
    public String getPositionKey() {
        return getNormalizedChromosome() + ":" + position;
    }

    /** Unieke sleutel van chromosoom, positie, REF en ALT. */
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
