package nl.neanderthaler.worker;

import nl.neanderthaler.model.ComparisonResult;
import nl.neanderthaler.model.Variant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariantComparator {

    /**
     * Sorts every variant into shared, only in human or only in Neanderthaler.
     *
     * @param human         the filtered human variants
     * @param neanderthaler the filtered Neanderthaler variants
     * @return the comparison with all categories filled
     */
    public ComparisonResult compare(List<Variant> human, List<Variant> neanderthaler) {
        Map<String, Variant> humanMap = toMap(human);
        Map<String, Variant> neanderthalerMap = toMap(neanderthaler);

        // All Neanderthaler positions without the allel to find a different one at the same spot
        Set<String> neanderthalerPositions = new HashSet<>();
        for (Variant v : neanderthalerMap.values()) {
            neanderthalerPositions.add(v.getPositionKey());
        }

        List<Variant> shared = new ArrayList<>();
        List<Variant> onlyInHuman = new ArrayList<>();
        List<Variant> differentAllele = new ArrayList<>();

        for (Variant h : humanMap.values()) {
            // Same key means same chromosome, position, REF and ALT so the variant is shared
            if (neanderthalerMap.containsKey(h.getKey())) {
                shared.add(h);
            } else {
                onlyInHuman.add(h);
                //  Both have a variant at this position, but the variant is different
                if (neanderthalerPositions.contains(h.getPositionKey())) {
                    differentAllele.add(h);
                }
            }
        }

        // Neanderthaler variants that the human does not have
        List<Variant> onlyInNeanderthaler = new ArrayList<>();
        for (Variant n : neanderthalerMap.values()) {
            if (!humanMap.containsKey(n.getKey())) {
                onlyInNeanderthaler.add(n);
            }
        }

        return new ComparisonResult(shared, onlyInHuman, onlyInNeanderthaler, differentAllele);
    }

    /** Maps each key to its variant, so duplicate lines count once. The order of the file is kept. */
    private Map<String, Variant> toMap(List<Variant> variants) {
        Map<String, Variant> map = new LinkedHashMap<>();
        for (Variant v : variants) {
            map.putIfAbsent(v.getKey(), v); // only the first variant with this key is kept
        }
        return map;
    }
}