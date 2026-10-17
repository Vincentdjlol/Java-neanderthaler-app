package nl.neanderthaler.worker;

import nl.neanderthaler.model.Variant;

import java.util.List;

public class VariantFilter {

    private final double minQuality;
    private final String chromosome; // null = alle chromosomen

    public VariantFilter(double minQuality, String chromosome) {
        this.minQuality = minQuality;
        this.chromosome = Variant.normalizeChromosome(chromosome);
    }

    public List<Variant> filter(List<Variant> variants) {
        return variants.stream()
                .filter(this::passesFilterField)
                .filter(this::passesQuality)
                .filter(this::passesChromosome)
                .toList();
    }

    /** FILTER moet PASS of "." zijn, anders heeft de variant-caller hem zelf afgekeurd. */
    private boolean passesFilterField(Variant variant) {
        String f = variant.getFilter();
        return f == null || f.equals("PASS") || f.equals(".");
    }

    private boolean passesQuality(Variant variant) {
        double q = variant.getQuality();
        if (Double.isNaN(q)) {
            return minQuality <= 0; // onbekende kwaliteit alleen doorlaten als er geen drempel is
        }
        return q >= minQuality;
    }

    private boolean passesChromosome(Variant variant) {
        return chromosome == null || chromosome.equals(variant.getNormalizedChromosome());
    }
}
