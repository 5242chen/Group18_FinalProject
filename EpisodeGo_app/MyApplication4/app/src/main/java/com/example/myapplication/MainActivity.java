package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText queryEditText;
    private TextView textView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryEditText = findViewById(R.id.searchInput);  // 初始化輸入欄位
        textView = findViewById(R.id.textView);          // 初始化文字顯示
    }

    // 搜尋按鈕點擊事件
    public void search(View view) {
        String query = queryEditText.getText().toString().trim();

        if (!query.isEmpty()) {
            textView.setText("Please wait a minute"); // 顯示提示文字
            performApiRequest(query);
        } else {
            Toast.makeText(this, "Please enter a query", Toast.LENGTH_SHORT).show();
        }
    }

    private void performApiRequest(String query) {
        ApiService apiService = RetrofitClient.getApiService();
        Glide.with(this)
                .load(R.drawable.logo3)  // Ensure logo3.gif is in res/drawable
                .into((ImageView) findViewById(R.id.logo));
        Call<HashMap<String, String>> call = apiService.search(query);

        call.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<String, String> data = response.body();
                    Log.d("API_Response", "Response: " + data);

                    // 跳轉到 ResultActivity 並傳遞結果
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("resultData", data.toString());
                    startActivity(intent);
                } else {
                    textView.setText(""); // 清空提示文字
                    Toast.makeText(MainActivity.this, "Response failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                textView.setText(""); // 清空提示文字
                Log.e("API_Error", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v != null && v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
