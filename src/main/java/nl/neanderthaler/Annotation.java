package nl.neanderthaler;

public class Annotation {

    private final String gene;
    private final String effect;
    private final String impact;

    public Annotation(String gene, String effect, String impact) {
        this.gene = gene;
        this.effect = effect;
        this.impact = impact;
    }

    public String getGene() { return gene; }
    public String getEffect() { return effect; }
    public String getImpact() { return impact; }
}