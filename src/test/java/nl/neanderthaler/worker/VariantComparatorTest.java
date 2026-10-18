package nl.neanderthaler.worker;

import nl.neanderthaler.model.ComparisonResult;
import nl.neanderthaler.model.Variant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VariantComparatorTest {

    private final VariantComparator comparator = new VariantComparator();

    private static Variant variant(String chrom, long pos, String ref, String alt) {
        return new Variant(chrom, pos, ".", ref, alt, 50, "PASS", null);
    }

    @Test
    void identicalVariantIsShared() {
        ComparisonResult r = comparator.compare(
                List.of(variant("chr1", 100, "A", "G")),
                List.of(variant("1", 100, "A", "G"))); // naming differs between files, matching should not

        assertEquals(1, r.getSharedCount());
        assertTrue(r.getOnlyInHuman().isEmpty());
        assertTrue(r.getOnlyInNeanderthaler().isEmpty());
    }

    @Test
    void differentPositionsAreOnlyInOne() {
        ComparisonResult r = comparator.compare(
                List.of(variant("1", 100, "A", "G")),
                List.of(variant("1", 200, "C", "T")));

        assertEquals(0, r.getSharedCount());
        assertEquals(1, r.getOnlyInHuman().size());
        assertEquals(1, r.getOnlyInNeanderthaler().size());
        assertTrue(r.getDifferentAllele().isEmpty());
    }

    @Test
    void samePositionDifferentAltIsDifferentAllele() {
        ComparisonResult r = comparator.compare(
                List.of(variant("1", 100, "A", "G")),
                List.of(variant("1", 100, "A", "T")));

        // differentAllele is a subset of onlyInHuman, not a separate bucket, so the variant shows up in both
        assertEquals(1, r.getOnlyInHuman().size());
        assertEquals(1, r.getDifferentAllele().size());
        assertEquals(0, r.getSharedCount());
    }

    @Test
    void duplicatesAreCountedOnce() {
        ComparisonResult r = comparator.compare(
                List.of(variant("1", 100, "A", "G"), variant("1", 100, "A", "G")),
                List.of(variant("1", 100, "A", "G")));

        assertEquals(1, r.getHumanTotal());
    }

    @Test
    void totalsAndPercentageAreCorrect() {
        ComparisonResult r = comparator.compare(
                List.of(variant("1", 100, "A", "G"), variant("1", 200, "C", "T"),
                        variant("1", 300, "G", "A"), variant("1", 400, "T", "C")),
                List.of(variant("1", 100, "A", "G"), variant("1", 500, "A", "C")));

        assertEquals(4, r.getHumanTotal());
        assertEquals(2, r.getNeanderthalerTotal());
        assertEquals(25.0, r.getSharedPercentage(), 1e-9);
    }

    @Test
    void emptyInputDoesNotDivideByZero() {
        ComparisonResult r = comparator.compare(List.of(), List.of());
        assertEquals(0.0, r.getSharedPercentage()); // guards against 0/0 producing NaN
    }
}