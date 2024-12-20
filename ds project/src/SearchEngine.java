import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchEngine {
    private List<WebPage> webPages;
    private List<Keyword> predefinedKeywords;

    public SearchEngine() {
        this.webPages = new ArrayList<>();
        this.predefinedKeywords = new ArrayList<>();
        initializePredefinedKeywords();
    }

    // Initialize predefined keywords with their weights
    private void initializePredefinedKeywords() {
        predefinedKeywords.add(new Keyword("friends", 3));
        predefinedKeywords.add(new Keyword("friends", 3)); // Alternative Term
        predefinedKeywords.add(new Keyword("actor", 1));
        predefinedKeywords.add(new Keyword("fictional character", 1.5));
        predefinedKeywords.add(new Keyword("usa", 2));
        predefinedKeywords.add(new Keyword("new york", 2));
        predefinedKeywords.add(new Keyword("hollywood", 1.5));
        predefinedKeywords.add(new Keyword("emmy award", 1));
        predefinedKeywords.add(new Keyword("apartment", 2));
        predefinedKeywords.add(new Keyword("central park", 2));
        predefinedKeywords.add(new Keyword("sitcom", 2.5));
        predefinedKeywords.add(new Keyword("friend", 1));
        predefinedKeywords.add(new Keyword("old friend", 1)); // Alternative Term
        predefinedKeywords.add(new Keyword("season", 1));
        predefinedKeywords.add(new Keyword("movie", -3));
        predefinedKeywords.add(new Keyword("model", -3));
        predefinedKeywords.add(new Keyword("italy", -1));
        predefinedKeywords.add(new Keyword("instagram", -3));
        predefinedKeywords.add(new Keyword("facebook", -3));
        predefinedKeywords.add(new Keyword("university", -1));
        predefinedKeywords.add(new Keyword("animation", -2));
        predefinedKeywords.add(new Keyword("game", -2));
        predefinedKeywords.add(new Keyword("sports", -1));
    }

    public void buildIndex(List<WebPage> webPages) {
        this.webPages.addAll(webPages);
    }

    public List<SearchResult> search(String query) {
        // Process user input into keywords
        List<Keyword> queryKeywords = new ArrayList<>();
        String[] tokens = query.trim().toLowerCase().split("\\s+");
        for (String t : tokens) {
            queryKeywords.add(new Keyword(t, 1.0)); // Default weight for user input
        }

        // Combine predefined keywords with query keywords
        List<Keyword> allKeywords = new ArrayList<>(predefinedKeywords);
        allKeywords.addAll(queryKeywords);

        List<SearchResult> results = new ArrayList<>();

        for (WebPage page : webPages) {
            double pageScore = page.calculateScore(allKeywords);
            if (pageScore > 0) { // Only include pages with positive scores
                results.add(new SearchResult(page, pageScore));
            }
        }

        // Sort results by descending score
        Collections.sort(results, Comparator.comparingDouble(SearchResult::getScore).reversed());

        // Limit to top 3 results
        if (results.size() > 3) {
            results = results.subList(0, 3);
        }

        return results;
    }
}
