package nl.neanderthaler.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariantTest {

    private static Variant variant(String chrom, String ref, String alt) {
        return new Variant(chrom, 100, ".", ref, alt, 50, "PASS", null);
    }

    @Test
    void normalizeChromosomeHandlesAllTheNamingStyles() {
        // UCSC style (chr1) and Ensembl style (1) need to end up identical
        assertEquals("1", Variant.normalizeChromosome("chr1"));
        assertEquals("1", Variant.normalizeChromosome("CHR1"));
        assertEquals("21", Variant.normalizeChromosome(" chr21 "));
        assertEquals("MT", Variant.normalizeChromosome("chrM"));
        assertNull(Variant.normalizeChromosome(null));
    }

    @Test
    void typeIsDeterminedFromRefAndAlt() {
        assertEquals(VariantType.SNP, variant("1", "A", "G").getType());
        assertEquals(VariantType.INSERTION, variant("1", "A", "AT").getType());
        assertEquals(VariantType.DELETION, variant("1", "AT", "A").getType());
        assertEquals(VariantType.OTHER, variant("1", "AT", "GC").getType()); // MNP
    }

    @Test
    void keysUseNormalizedChromosome() {
        Variant v = variant("chr1", "A", "G");

        assertEquals("1:100", v.getPositionKey());
        assertEquals("1:100:A>G", v.getKey());
    }

    @Test
    void equalsOnlyLooksAtTheKey() {
        // Identity is chromosome, position, REF and ALT. ID, QUAL and FILTER are deliberately ignored
        Variant a = new Variant("chr1", 100, "rs1", "A", "G", 50, "PASS", null);
        Variant b = new Variant("1", 100, ".", "A", "G", 10, "LowQual", null);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, variant("1", "A", "T"));
    }
}