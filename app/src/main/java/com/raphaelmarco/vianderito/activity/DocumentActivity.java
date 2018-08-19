package com.raphaelmarco.vianderito.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.raphaelmarco.vianderito.R;

import java.util.HashMap;

public class DocumentActivity extends AppCompatActivity {

    ProgressBar progressBar;

    WebView webView;

    HashMap<String, String> links = new HashMap<>();

    {
        links.put("tos", "file:///android_asset/tos.html");
        links.put("privacy_policy", "file:///android_asset/privacy_policy.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_document);

        String name = getIntent().getStringExtra("name");

        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.webview);

        webView.setWebViewClient(new Client());

        if (links.containsKey(name)) {
            webView.loadUrl(links.get(name));
        }
    }

    class Client extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
