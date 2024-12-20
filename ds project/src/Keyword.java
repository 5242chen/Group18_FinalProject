public class Keyword {
    private String word;
    private double weight;

    public Keyword(String word, double weight) {
        this.word = word.toLowerCase();
        this.weight = weight;
    }

    public String getWord() {
        return word;
    }

    public double getWeight() {
        return weight;
    }
}
