package nl.neanderthaler.model;

import java.util.List;

public class ComparisonResult {

    private final List<Variant> shared;
    private final List<Variant> onlyInHuman;
    private final List<Variant> onlyInNeanderthaler;
    // human variants where the Neanderthaler has a DIFFERENT alt allele at the same position
    private final List<Variant> differentAllele;

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

    public int getSharedCount() {
        return shared.size();
    }

    public int getHumanTotal() {
        return shared.size() + onlyInHuman.size();
    }

    public int getNeanderthalerTotal() {
        return shared.size() + onlyInNeanderthaler.size();
    }

    /** Percentage of the human variants that the Neanderthaler carries as well. */
    public double getSharedPercentage() {
        int total = getHumanTotal();
        return total == 0 ? 0.0 : 100.0 * shared.size() / total;
    }
}