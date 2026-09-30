package nl.neanderthaler;

import java.util.List;

public class ComparisonResult {

    private final List<Variant> shared;
    private final List<Variant> onlyInHuman;
    private final List<Variant> onlyInNeanderthaler;

    public ComparisonResult(List<Variant> shared, List<Variant> onlyInHuman,
                            List<Variant> onlyInNeanderthaler) {
        this.shared = shared;
        this.onlyInHuman = onlyInHuman;
        this.onlyInNeanderthaler = onlyInNeanderthaler;
    }

    public List<Variant> getShared() { return shared; }
    public List<Variant> getOnlyInHuman() { return onlyInHuman; }
    public List<Variant> getOnlyInNeanderthaler() { return onlyInNeanderthaler; }

    public int getSharedCount() {
        return 0;
    }
}