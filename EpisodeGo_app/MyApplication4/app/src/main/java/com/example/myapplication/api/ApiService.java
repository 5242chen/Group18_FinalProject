package com.example.myapplication.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.HashMap;

public interface ApiService {
    @GET("api/search")
    Call<HashMap<String, String>> search(@Query("q") String query);
}
