import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WebPage {
    String url;
    String title;
    String content;
    List<String> subLinks; // Stores hyperlinks from the page

    public WebPage(String url) {
        this.url = url;
        this.subLinks = new ArrayList<>();
        fetchContentFromUrl();
    }

    private void fetchContentFromUrl() {
        try {
            Document doc = Jsoup.connect(url).get();
            this.title = doc.title();

            // Extract main content
            Elements paragraphs = doc.select("p");
            StringBuilder sb = new StringBuilder();
            for (Element p : paragraphs) {
                sb.append(p.text()).append(" ");
            }
            this.content = sb.toString();

            // Extract all hyperlinks
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String href = link.absUrl("href");
                // Basic validation to avoid self-links and non-HTTP links
                if (href.startsWith("http") && !href.equals(url)) {
                    subLinks.add(href);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching content from " + url + ": " + e.getMessage());
        }
    }

    // Calculate total score including sub-pages
    public double calculateScore(List<Keyword> predefinedKeywords) {
        double baseScore = calculateBaseScore(predefinedKeywords);
        double subPagesScore = calculateSubPagesScore(predefinedKeywords);
        return baseScore + subPagesScore;
    }

    // Calculate score based on the main page's content
    private double calculateBaseScore(List<Keyword> keywords) {
        double totalScore = 0.0;
        String titleLower = (title != null) ? title.toLowerCase() : "";
        String urlLower = url.toLowerCase();
        String contentLower = (content != null) ? content.toLowerCase() : "";

        for (Keyword k : keywords) {
            String w = k.getWord();
            double baseWeight = k.getWeight();

            int titleCount = countOccurrences(titleLower, w);
            int urlCount = countOccurrences(urlLower, w);
            int contentCount = countOccurrences(contentLower, w);

            // Weighted scoring: Title ×3, URL ×2, Content ×1
            double weightedScore = (titleCount * baseWeight * 3)
                    + (urlCount * baseWeight * 2)
                    + (contentCount * baseWeight * 1);

            totalScore += weightedScore;
        }

        return totalScore;
    }

    // Calculate score based on sub-pages, each weighted at 0.5
    private double calculateSubPagesScore(List<Keyword> keywords) {
        if (subLinks.isEmpty()) {
            return 0.0;
        }

        // Randomly select up to 5 sub-links
        List<String> randomSubLinks = getRandomSubLinks(5);
        double totalSubScore = 0.0;

        for (String subUrl : randomSubLinks) {
            WebPage subPage = new WebPage(subUrl);
            double subScore = subPage.calculateBaseScore(keywords);
            totalSubScore += subScore;
        }

        // Apply sub-page weight
        return totalSubScore * 0.5;
    }

    // Helper method to randomly select sub-links
    private List<String> getRandomSubLinks(int count) {
        List<String> result = new ArrayList<>(subLinks);
        Random random = new Random();

        if (result.size() <= count) {
            return result;
        }

        List<String> selected = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(result.size());
            selected.add(result.remove(index));
        }
        return selected;
    }

    // Counts the number of occurrences of a word in a text
    private int countOccurrences(String text, String word) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }
}
