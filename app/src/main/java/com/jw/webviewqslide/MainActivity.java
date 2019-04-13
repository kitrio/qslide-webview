package com.jw.webviewqslide;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lge.app.floating.FloatableActivity;

public class MainActivity extends FloatableActivity {

    private WebView mWebView;
    private WebSettings mWebSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webViewMain);
        //mWebView = (WebView) findViewById(R.id.webViewMain);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSetting = mWebView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);

        mWebView.loadUrl("https://m.naver.com");
    }


}
