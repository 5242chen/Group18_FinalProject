import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleSearchService {
    private static final String API_KEY = "YOUR_GOOGLE_API_KEY";
    private static final String SEARCH_ENGINE_ID = "YOUR_SEARCH_ENGINE_ID";

    public List<String> getTop15Urls(String query) throws Exception {
        // 將查詢字串編碼
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        // Google Custom Search API URL (一次可取得10筆, 可用start參數取得更多)
        // 例如：&start=1（第1頁）、&start=11（第2頁），加起來最多可拿到20筆
        String apiUrl = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY 
                        + "&cx=" + SEARCH_ENGINE_ID
                        + "&q=" + encodedQuery + "&num=10";

        List<String> urls = new ArrayList<>();

        // 第一次取得前10筆
        urls.addAll(fetchUrlsFromApi(apiUrl));

        if (urls.size() < 15) {
            // 再取下一頁 (第11到20筆)
            String apiUrlPage2 = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY 
                                 + "&cx=" + SEARCH_ENGINE_ID
                                 + "&q=" + encodedQuery + "&num=10&start=11";
            List<String> secondBatch = fetchUrlsFromApi(apiUrlPage2);
            urls.addAll(secondBatch);
        }

        // 取前15個
        if (urls.size() > 15) {
            urls = urls.subList(0, 15);
        }

        return urls;
    }

    private List<String> fetchUrlsFromApi(String apiUrl) throws Exception {
        List<String> urls = new ArrayList<>();
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) { 
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseBuilder.append(inputLine);
            }
            in.close();
            
            JSONObject json = new JSONObject(responseBuilder.toString());
            JSONArray items = json.optJSONArray("items");
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String link = item.optString("link");
                    if (link != null && !link.isEmpty()) {
                        urls.add(link);
                    }
                }
            }
        }

        return urls;
    }
}
