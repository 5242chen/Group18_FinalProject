package com.example.myapplication.models;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://your-backend-url.com"; // 替換為您的後端服務地址

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 指定後端服務的根 URL
                    .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析 JSON
                    .build();
        }
        return retrofit;
    }
}
