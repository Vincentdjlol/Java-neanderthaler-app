package nl.neanderthaler.model;

import java.util.Objects;

/**
 * Explanation this class:
 * One variant from a VCF line that the person really has.
 * When a line has more than one variant, each one becomes its own Variant.
 */
public class Variant {

    // The columns of one VCF line that we use
    private final String chromosome;
    private final long position;
    private final String id;
    private final String ref;
    private final String alt;
    private final double quality;   // Double.NaN when QUAL is a dot
    private final String filter;
    private final Annotation annotation; // gene, effect and impact, null when the line has no annotation

    /**
     * Makes one variant from the columns of a VCF line.
     *
     * @param chromosome the chromosome as written in the VCF
     * @param position   the position on the chromosome
     * @param id         the ID column, often a dot
     * @param ref        the base or bases in the reference genome
     * @param alt        the base or bases the individual has instead
     * @param quality    the QUAL value, Double.NaN when it is a dot
     * @param filter     the FILTER value, for example PASS
     * @param annotation the gene, effect and impact, or null
     */
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
    public String getRef() { return ref; }
    public String getAlt() { return alt; }
    public double getQuality() { return quality; }
    public String getFilter() { return filter; }
    public Annotation getAnnotation() { return annotation; }

    /**
     * chr1, Chr1 and 1 all become 1, so files with different naming still match.
     *
     * @param chrom the chromosome name from the VCF
     * @return the name without chr, or null when chrom is null
     */
    public static String normalizeChromosome(String chrom) {
        if (chrom == null) {
            return null;
        }
        String c = chrom.trim(); // removes spaces around the name
        if (c.toLowerCase().startsWith("chr")) {
            c = c.substring(3); // removes the chr in front
        }
        if (c.equalsIgnoreCase("M")) {
            c = "MT"; // the mitochondrial chromosome has two names, we always use MT
        }
        return c.toUpperCase();
    }

    /**
     * The chromosome name without chr, so it can be compared with other files
     *
     * @return the cleaned up chromosome name
     */
    public String getNormalizedChromosome() {
        return normalizeChromosome(chromosome);
    }

    /**
     * Looks at the length of REF and ALT to decide what kind of variant it is
     *
     * @return SNP, INSERTION, DELETION or OTHER
     */
    public VariantType getType() {
        if (ref.length() == 1 && alt.length() == 1) {
            return VariantType.SNP; // one base changed into another base
        }
        if (alt.length() > ref.length()) {
            return VariantType.INSERTION; // bases were added
        }
        if (alt.length() < ref.length()) {
            return VariantType.DELETION; // bases were removed
        }
        return VariantType.OTHER; // for example an MNP such as AT>GC
    }

    /**
     * Position key without the allele, used to find "same position, different allele".
     *
     * @return the chromosome and position, for example 1:100
     */
    public String getPositionKey() {
        return getNormalizedChromosome() + ":" + position;
    }

    /**
     * Unique key built from chromosome, position, REF and ALT.
     *
     * @return the key, for example 1:100:A>G
     */
    public String getKey() {
        return getPositionKey() + ":" + ref + ">" + alt;
    }

    /**
     * Two variants are the same when their key is the same
     *
     * @param o the object to compare with
     * @return true when both variants have the same key
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variant other)) return false;
        return getKey().equals(other.getKey());
    }

    /**
     * Has to match equals, so the variant works in a HashMap or HashSet
     *
     * @return the hash of the key
     */
    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    /**
     * How the variant is shown when you print it
     *
     * @return the variant as text, for example chr1:100 A>G
     */
    @Override
    public String toString() {
        return chromosome + ":" + position + " " + ref + ">" + alt;
    }
}