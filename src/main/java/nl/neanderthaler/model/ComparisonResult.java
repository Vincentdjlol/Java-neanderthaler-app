package nl.neanderthaler.model;

import java.util.List;

/**
 * The outcome of comparing the human and Neanderthaler variants.
 */
public class ComparisonResult {

    private final List<Variant> shared;
    private final List<Variant> onlyInHuman;
    private final List<Variant> onlyInNeanderthaler;
    // human variants where the Neanderthaler has a DIFFERENT alt allele at the same position
    private final List<Variant> differentAllele;

    /**
     * Stores the four lists made by the VariantComparator.
     *
     * @param shared              variants that both have
     * @param onlyInHuman         variants that only the human has
     * @param onlyInNeanderthaler variants that only the Neanderthaler has
     * @param differentAllele     human variants where the Neanderthaler has a different variant at the same position
     */
    public ComparisonResult(List<Variant> shared, List<Variant> onlyInHuman,
                            List<Variant> onlyInNeanderthaler, List<Variant> differentAllele) {
        this.shared = shared;
        this.onlyInHuman = onlyInHuman;
        this.onlyInNeanderthaler = onlyInNeanderthaler;
        this.differentAllele = differentAllele;
    }

    public List<Variant> getShared() { return shared; }
    public List<Variant> getOnlyInHuman() { return onlyInHuman; }
    public List<Variant> getOnlyInNeanderthaler() { return onlyInNeanderthaler; }
    public List<Variant> getDifferentAllele() { return differentAllele; }

    /**
     * Counts the shared variants.
     *
     * @return the number of shared variants
     */
    public int getSharedCount() {
        return shared.size();
    }

    /**
     * Counts all human variants.
     *
     * @return the shared variants plus the variants only in human
     */
    public int getHumanTotal() {
        return shared.size() + onlyInHuman.size();
    }

    /**
     * Counts all Neanderthaler variants.
     *
     * @return the shared variants plus the variants only in Neanderthaler
     */
    public int getNeanderthalerTotal() {
        return shared.size() + onlyInNeanderthaler.size();
    }

    /**
     * Percentage of the human variants that the Neanderthaler carries as well.
     *
     * @return the percentage, or 0 when there are no human variants
     */
    public double getSharedPercentage() {
        int total = getHumanTotal();
        return total == 0 ? 0.0 : 100.0 * shared.size() / total; // stops dividing by zero
    }
}