package google.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class GoogleQueryService {
    private String searchKeyword;
    private String url;

    public GoogleQueryService() {
        // 空的構造函數
    }

    public HashMap<String, String> search(String searchKeyword) throws IOException {
        this.searchKeyword = searchKeyword;

        try {
            String encodeKeyword = URLEncoder.encode(searchKeyword, "utf-8");
            this.url = "https://www.google.com/search?q=" + encodeKeyword + "&oe=utf8&num=20";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 重新獲取內容
        String content = fetchContent();

        HashMap<String, String> retVal = new HashMap<>();

        // 使用 Jsoup 解析 HTML 字符串
        Document doc = Jsoup.parse(content);

        // 選擇特定元素
        Elements lis = doc.select("div.kCrYT");

        for (Element li : lis) {
            try {
                String citeUrl = li.select("a").attr("href").replace("/url?q=", "").split("&")[0];
                String title = li.select("a").select(".vvjwJb").text();

                if (title.equals("")) {
                    continue;
                }

                System.out.println("Title: " + title + " , url: " + citeUrl);

                // 將標題和 URL 放入 HashMap
                retVal.put(title, citeUrl);

            } catch (IndexOutOfBoundsException e) {
                // 忽略例外
            }
        }

        return retVal;
    }

    private String fetchContent() throws IOException {
        StringBuilder retVal = new StringBuilder();

        // 使用隨機的 User-Agent 避免被 Google 檢測為爬蟲
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0"
        };
        String randomUserAgent = userAgents[new Random().nextInt(userAgents.length)];

        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        conn.setRequestProperty("User-agent", randomUserAgent);  // 使用隨機 User-Agent

        // 不使用緩存
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Pragma", "no-cache");

        InputStream in = conn.getInputStream();
        InputStreamReader inReader = new InputStreamReader(in, "utf-8");
        BufferedReader bufReader = new BufferedReader(inReader);
        String line;

        while ((line = bufReader.readLine()) != null) {
            retVal.append(line);
        }
        bufReader.close();

        return retVal.toString();
    }
}