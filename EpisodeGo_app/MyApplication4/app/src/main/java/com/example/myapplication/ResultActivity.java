package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultTextView = findViewById(R.id.resultTextView); // 顯示結果的 TextView

        // 獲取傳遞的結果
        String result = getIntent().getStringExtra("resultData");


        if (result != null && !result.isEmpty()) {
            // 顯示結果
            resultTextView.setText(result);
        } else {
            // 顯示無結果提示
            resultTextView.setText("No result available");
            Log.w("ResultActivity", "Result data is empty or null");
        }
    }
}
