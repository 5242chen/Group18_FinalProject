public class SearchResult implements Comparable<SearchResult> {
    private WebPage webPage;
    private double score;

    public SearchResult(WebPage webPage, double score) {
        this.webPage = webPage;
        this.score = score;
    }

    @Override
    public int compareTo(SearchResult o) {
        return Double.compare(o.score, this.score);
    }

    public WebPage getWebPage() {
        return webPage;
    }

    public double getScore() {
        return score;
    }
}
