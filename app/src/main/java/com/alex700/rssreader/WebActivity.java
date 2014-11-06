package com.alex700.rssreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class WebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        String s = getIntent().getStringExtra("url");
        WebView webView  = (WebView) findViewById(R.id.webView);
        webView.loadUrl(s);
    }

}
