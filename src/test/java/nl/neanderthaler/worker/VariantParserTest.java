package nl.neanderthaler.worker;

import nl.neanderthaler.model.Annotation;
import nl.neanderthaler.model.Variant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VariantParserTest {

    private final VariantParser parser = new VariantParser();

    // VCF is strictly tab separated, the parser splits on \t only
    private static String line(String... fields) {
        return String.join("\t", fields);
    }

    private List<Variant> parse(String... fields) {
        return parser.parse(List.of(line(fields)));
    }

    @Test
    void heterozygousSnpGivesOneVariant() {
        List<Variant> result = parse("chr1", "100", "rs1", "A", "G", "50", "PASS", ".", "GT", "0/1");

        assertEquals(1, result.size());
        assertEquals("1:100:A>G", result.get(0).getKey()); // key uses the normalized chromosome
        assertEquals(50.0, result.get(0).getQuality());
    }

    @Test
    void noVariantWhenSampleDoesNotCarryAnAlt() {
        assertTrue(parse("chr1", "100", ".", "A", "G", "50", "PASS", ".", "GT", "0/0").isEmpty()); // hom ref
        assertTrue(parse("chr1", "100", ".", "A", "G", "50", "PASS", ".", "GT", "./.").isEmpty());
        assertTrue(parse("chr1", "100", ".", "A", ".", "50", "PASS", ".", "GT", "0/0").isEmpty()); // monomorphic site
    }

    @Test
    void multiAllelicLineIsSplitIntoSeparateVariants() {
        // Multi-allelic records are decomposed into one Variant per carried ALT
        List<Variant> result = parse("chr1", "100", ".", "A", "C,G", "50", "PASS", ".", "GT", "1/2");

        assertEquals(2, result.size());
        assertEquals("C", result.get(0).getAlt());
        assertEquals("G", result.get(1).getAlt());
    }

    @Test
    void onlyCarriedAltIsReturned() {
        // GT indexes into the ALT list, so 0/2 means only G is carried even though C is listed
        List<Variant> result = parse("chr1", "100", ".", "A", "C,G", "50", "PASS", ".", "GT", "0/2");

        assertEquals(1, result.size());
        assertEquals("G", result.get(0).getAlt());
    }

    @Test
    void gtDoesNotHaveToBeFirstInFormat() {
        // GT position is looked up in FORMAT instead of assumed to be first
        assertEquals(1, parse("chr1", "100", ".", "A", "G", "50", "PASS", ".", "DP:GT", "12:0|1").size());
    }

    @Test
    void spanningDeletionAndSymbolicAlleleAreSkipped() {
        // Spanning deletions and symbolic alleles have no comparable sequence and are dropped
        List<Variant> result = parse("chr1", "100", ".", "A", "*,<DEL>,G", "50", "PASS", ".");

        assertEquals(1, result.size());
        assertEquals("G", result.get(0).getAlt());
    }

    @Test
    void qualityDotBecomesNaN() {
        List<Variant> result = parse("chr1", "100", ".", "A", "G", ".", "PASS", ".", "GT", "0/1");
        assertTrue(Double.isNaN(result.get(0).getQuality())); // NaN != NaN, so assertEquals is useless here
    }

    @Test
    void brokenLinesAreSkippedButTheRestIsStillParsed() {
        List<Variant> result = parser.parse(List.of(
                line("chr1", "abc", ".", "A", "G", "50", "PASS", ".", "GT", "0/1"),
                line("chr1", "100", "A"),
                line("chr1", "200", ".", "C", "T", "50", "PASS", ".", "GT", "0/1")));

        // A malformed record is logged and skipped, it should not abort the whole file
        assertEquals(1, result.size());
        assertEquals(200, result.get(0).getPosition());
    }

    @Test
    void annotationIsReadForMatchingAllele() {
        // ANN holds one entry per allele, this checks the match on allele rather than taking the first entry
        String info = "DP=10;ANN=C|synonymous_variant|LOW|GENE1,G|missense_variant|MODERATE|BRCA1";
        Annotation ann = parse("chr1", "100", ".", "A", "G", "50", "PASS", info, "GT", "0/1").get(0).getAnnotation();

        assertEquals("BRCA1", ann.getGene());
        assertEquals("missense_variant", ann.getEffect());
        assertEquals("MODERATE", ann.getImpact());
    }
}