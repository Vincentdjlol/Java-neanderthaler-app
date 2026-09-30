package nl.neanderthaler;

public class Variant {

    private final String chromosome;
    private final long position;
    private final String id;
    private final String ref;
    private final String alt;
    private final double quality;
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
    public String getRef() { return ref; }
    public String getAlt() { return alt; }
    public double getQuality() { return quality; }
    public Annotation getAnnotation() { return annotation; }

    public VariantType getType() {
        return null;
    }

    public String getKey() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}