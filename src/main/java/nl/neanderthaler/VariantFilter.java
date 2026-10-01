package nl.neanderthaler;

import java.util.List;

public class VariantFilter {

    private final double minQuality;
    private final String chromosome;

    public VariantFilter(double minQuality, String chromosome) {
        this.minQuality = minQuality;
        this.chromosome = chromosome;
    }

    public List<Variant> filter(List<Variant> variants) {
        return null;
    }

    private boolean passesQuality(Variant variant) {
        return false;
    }

    private boolean passesChromosome(Variant variant) {
        return false;
    }
}