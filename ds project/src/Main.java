import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static SearchEngine searchEngine;

    public static void main(String[] args) throws IOException {
        // Initialize web pages (Friends characters' Wikipedia pages)
        List<WebPage> pages = new ArrayList<>();
        pages.add(new WebPage("https://en.wikipedia.org/wiki/Rachel_Green"));
        pages.add(new WebPage("https://en.wikipedia.org/wiki/Monica_Geller"));
        pages.add(new WebPage("https://en.wikipedia.org/wiki/Phoebe_Buffay"));
        pages.add(new WebPage("https://en.wikipedia.org/wiki/Joey_Tribbiani"));
        pages.add(new WebPage("https://en.wikipedia.org/wiki/Ross_Geller"));
        pages.add(new WebPage("https://en.wikipedia.org/wiki/Chandler_Bing"));

        // Initialize search engine and index web pages
        searchEngine = new SearchEngine();
        searchEngine.buildIndex(pages);

        // Start HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/search", new SearchHandler());
        server.setExecutor(null); // Use default executor
        server.start();
        System.out.println("Server started at http://localhost:8000/");
    }

    // Handler for the homepage, displays the search form
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<!DOCTYPE html>"
                    + "<html lang=\"en\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Friends Character Search</title>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background: #f9f9f9; margin: 0; padding: 0; }"
                    + "header { background: #4CAF50; color: #fff; padding: 20px; text-align: center; }"
                    + "main { width: 80%; margin: 50px auto; background: #fff; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
                    + "h1 { margin-top: 0; }"
                    + "form { display: flex; flex-direction: row; align-items: center; gap: 10px; }"
                    + "input[type=text] { flex: 1; padding: 8px; font-size: 16px; }"
                    + "input[type=submit] { padding: 8px 16px; font-size: 16px; background: #4CAF50; color: #fff; border: none; cursor: pointer; }"
                    + "input[type=submit]:hover { background: #45a049; }"
                    + "p { font-size: 18px; line-height: 1.5; color: #555; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<header><h1>Friends Character Search</h1></header>"
                    + "<main>"
                    + "<p>Welcome! This simple search engine allows you to find information about characters from the TV show \"Friends\". Just type in any character name or related terms (e.g., \"Rachel\", \"Central Park\", \"Friends\") and hit search!</p>"
                    + "<form action=\"/search\" method=\"post\">"
                    + "  <input name=\"query\" type=\"text\" placeholder=\"Type a name or keyword (e.g. Rachel)\" required/>"
                    + "  <input type=\"submit\" value=\"Search\"/>"
                    + "</form>"
                    + "</main>"
                    + "</body></html>";

            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // Handler for processing search requests and displaying results
    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                byte[] data = exchange.getRequestBody().readAllBytes();
                String formData = new String(data, StandardCharsets.UTF_8);
                String query = "";
                if (formData.startsWith("query=")) {
                    query = URLDecoder.decode(formData.substring("query=".length()), "UTF-8");
                }

                List<SearchResult> results = searchEngine.search(query);
                String response;

                if (results.isEmpty()) {
                    response = "<!DOCTYPE html><html lang=\"en\"><head><title>No Results</title>"
                             + "<style>"
                             + "body { font-family: Arial, sans-serif; background: #f9f9f9; margin:0; padding:0; }"
                             + "header { background: #4CAF50; color: #fff; padding: 20px; text-align: center; }"
                             + "main { width: 80%; margin: 50px auto; background: #fff; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
                             + "h1 { margin-top: 0; }"
                             + "a { color: #007BFF; text-decoration: none; font-weight: bold; }"
                             + "a:hover { color: #28A745; text-decoration: underline; }"
                             + ".back-link { display: inline-block; margin-top: 20px; background: #4CAF50; color: #fff; padding: 8px 16px; text-decoration: none; }"
                             + ".back-link:hover { background: #45a049; }"
                             + "</style>"
                             + "</head><body>"
                             + "<header><h1>No Results Found</h1></header>"
                             + "<main>"
                             + "<p>No pages match your query: \"" + query + "\"</p>"
                             + "<a class=\"back-link\" href=\"/\">Back to Search</a>"
                             + "</main>"
                             + "</body></html>";
                } else {
                    // Get top 3 results
                    int displayCount = Math.min(results.size(), 3);
                    List<SearchResult> topResults = results.subList(0, displayCount);

                    // Display the top result
                    SearchResult topResult = topResults.get(0);

                    // Display the next two suggestions, if available
                    StringBuilder suggestions = new StringBuilder();
                    if (displayCount > 1) {
                        suggestions.append("<h3>You may also consider:</h3><ul>");
                        for (int i = 1; i < displayCount; i++) {
                            SearchResult r = topResults.get(i);
                            suggestions.append("<li><a href=\"").append(r.getWebPage().url)
                                       .append("\" target=\"_blank\">").append(r.getWebPage().title)
                                       .append("</a></li>");
                        }
                        suggestions.append("</ul>");
                    }

                    response = "<!DOCTYPE html>"
                             + "<html lang=\"en\">"
                             + "<head>"
                             + "<meta charset=\"UTF-8\">"
                             + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                             + "<title>Best Match</title>"
                             + "<style>"
                             + "body { font-family: Arial, sans-serif; background: #f9f9f9; margin:0; padding:0; }"
                             + "header { background: #4CAF50; color: #fff; padding: 20px; text-align: center; }"
                             + "main { width: 80%; margin: 50px auto; background: #fff; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
                             + "h1, h2, h3 { margin-top: 0; }"
                             + "a { color: #007BFF; text-decoration: none; font-weight: bold; }" // Blue and bold
                             + "a:hover { color: #28A745; text-decoration: underline; }" // Green on hover
                             + ".back-link { display: inline-block; margin-top: 20px; background: #4CAF50; color: #fff; padding: 8px 16px; text-decoration: none; }"
                             + ".back-link:hover { background: #45a049; }"
                             + "li { margin: 8px 0; }"
                             + "</style>"
                             + "</head>"
                             + "<body>"
                             + "<header><h1>Best Match</h1></header>"
                             + "<main>"
                             + "<h2>The most relevant page for \"" + query + "\"</h2>"
                             + "<p><strong>Title:</strong> " + topResult.getWebPage().title + "</p>"
                             + "<p><strong>URL:</strong> <a href=\"" + topResult.getWebPage().url + "\" target=\"_blank\">"
                             + topResult.getWebPage().url + "</a></p>"
                             + suggestions.toString()
                             + "<a class=\"back-link\" href=\"/\">Back to Search</a>"
                             + "</main>"
                             + "</body></html>";
                }

                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                // For non-POST requests, redirect to home
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
            }
        }
    }
}
