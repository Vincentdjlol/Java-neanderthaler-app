package nl.neanderthaler.worker;

import nl.neanderthaler.model.Variant;

import java.util.List;

public class VariantFilter {

    private final double minQuality;
    private final String chromosome; // null means all chromosomes

    public VariantFilter(double minQuality, String chromosome) {
        this.minQuality = minQuality;
        this.chromosome = Variant.normalizeChromosome(chromosome);
    }

    /**
     * Keeps only the variants that pass the FILTER field, the quality threshold and the chosen chromosome.
     *
     * @param variants the parsed variants
     * @return the variants that are left
     */
    public List<Variant> filter(List<Variant> variants) {
        return variants.stream()
                .filter(this::passesFilterField)
                .filter(this::passesQuality)
                .filter(this::passesChromosome)
                .toList();
    }

    /** FILTER has to be PASS or ".", otherwise the variant caller rejected the variant itself. */
    private boolean passesFilterField(Variant variant) {
        String f = variant.getFilter();
        return f == null || f.equals("PASS") || f.equals(".");
    }

    private boolean passesQuality(Variant variant) {
        double q = variant.getQuality();
        if (Double.isNaN(q)) {
            return minQuality <= 0; // unknown quality only gets through when there is no threshold
        }
        return q >= minQuality;
    }

    private boolean passesChromosome(Variant variant) {
        return chromosome == null || chromosome.equals(variant.getNormalizedChromosome());
    }
}