package nl.neanderthaler.worker;

import nl.neanderthaler.model.Variant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VariantFilterTest {

    private static Variant variant(String chrom, double qual, String filter) {
        return new Variant(chrom, 100, ".", "A", "G", qual, filter, null);
    }

    @Test
    void passAndDotFilterArePassed() {
        List<Variant> input = List.of(variant("1", 50, "PASS"), variant("1", 50, "."));
        assertEquals(2, new VariantFilter(0, null).filter(input).size());
    }

    @Test
    void rejectedByVariantCallerIsRemoved() {
        assertTrue(new VariantFilter(0, null).filter(List.of(variant("1", 50, "LowQual"))).isEmpty());
    }

    @Test
    void qualityBelowThresholdIsRemoved() {
        List<Variant> result = new VariantFilter(30, null)
                .filter(List.of(variant("1", 29.9, "PASS"), variant("1", 30, "PASS")));

        assertEquals(1, result.size());
        assertEquals(30.0, result.get(0).getQuality()); // threshold is inclusive
    }

    @Test
    void unknownQualityOnlyPassesWithoutThreshold() {
        // Missing QUAL is NaN, and every comparison with NaN is false. That only matters once there is a threshold
        List<Variant> input = List.of(variant("1", Double.NaN, "PASS"));

        assertEquals(1, new VariantFilter(0, null).filter(input).size());
        assertTrue(new VariantFilter(10, null).filter(input).isEmpty());
    }

    @Test
    void chromosomeFilterIgnoresChrPrefix() {
        List<Variant> input = List.of(variant("chr21", 50, "PASS"), variant("21", 50, "PASS"), variant("chr22", 50, "PASS"));

        assertEquals(2, new VariantFilter(0, "21").filter(input).size());
        assertEquals(2, new VariantFilter(0, "chr21").filter(input).size());
    }

    @Test
    void nonExistingChromosomeGivesEmptyList() {
        // A typo in --chrom gives an empty result rather than an exception
        assertTrue(new VariantFilter(0, "99").filter(List.of(variant("1", 50, "PASS"))).isEmpty());
    }
}