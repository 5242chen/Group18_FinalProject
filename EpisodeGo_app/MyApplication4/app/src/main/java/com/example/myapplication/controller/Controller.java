package com.example.myapplication.controller;

import com.example.myapplication.service.SearchEngine;
import com.example.myapplication.model.SearchResult;
import com.example.myapplication.service.Service;
import com.example.myapplication.model.WebPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 根據你的前端地址調整
public class Controller {

    @Autowired
    private Service googleQueryService;
    private SearchEngine searchEngine;


    @GetMapping("/search")
    public Map<String, String> search(@RequestParam("q") String query) {
        try {
            // 1. 先抓原始(標題, URL) 不論輸入關鍵字為何
            Map<String, String> rawResults = googleQueryService.search(query);

            // 2. 建立 WebPage 清單時，檢查是否為合格連結
            List<WebPage> pages = new ArrayList<>();
            for (Map.Entry<String, String> entry : rawResults.entrySet()) {
                String title = entry.getKey();
                String url = entry.getValue();

                // 檢查或修正 url
                String fixedUrl = fixDoubleEncoding(url);
                if (!isValidUrl(fixedUrl)) {
                    // 若解析失敗或仍有問題，跳過此條
                    continue;
                }

                pages.add(new WebPage(fixedUrl, title));
            }

            // 3. 交給 SearchEngine 排序 + 挑前 15 + 維基百科最多 3
            List<SearchResult> sortedList = searchEngine.searchAndSort(pages, query);

            // 4. 檢查結果是否「全部分數都 <= 5 分」
            boolean allBelowOrEq5 = true;
            for (SearchResult sr : sortedList) {
                if (sr.getScore() > 3) {
                    allBelowOrEq5 = false;
                    break;
                }
            }
            if (allBelowOrEq5) {
                // 全部皆不相關 => 回傳空集合
                return new LinkedHashMap<>();
            }

            // 5. 正常回傳 => Map<Title, URL>
            Map<String, String> response = new LinkedHashMap<>();
            for (SearchResult sr : sortedList) {
                String t = sr.getWebPage().getTitle();
                String u = sr.getWebPage().getUrl();
                response.put(t, u);
            }

            return response;

        } catch (IOException e) {
            e.printStackTrace();
            // 發生錯誤 => 回傳空
            return new LinkedHashMap<>();
        }
    }

    /**
     * 檢查 URL 是否可被正常解析 (無非法字元、多重編碼)
     */
    private boolean isValidUrl(String url) {
        try {
            // 嘗試用 new URL(...) 檢查是否格式正確
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 若 URL 中含有 "%25" (可能是二次編碼的 '%'),
     * 可以嘗試替換成 "%" 再做一次檢查。
     *
     * 可再視情況多做 decode。
     */
    private String fixDoubleEncoding(String url) {
        if (url.contains("%25")) {
            // 簡單做一次替換，把 "%25" -> "%"
            // 須注意有些情況會再次衍生新的問題
            return url.replaceAll("%25", "%");
        }
        return url;
    }
}