package com.example.erox.running;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webpage);

        WebView wb = findViewById(R.id.webview);
        wb.loadUrl("https://peaceful-peak-99231.herokuapp.com/about");
    }
}
