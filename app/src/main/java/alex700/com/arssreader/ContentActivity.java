package alex700.com.arssreader;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class ContentActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        String s = getIntent().getStringExtra("url");
        WebView webView  = (WebView) findViewById(R.id.webView);
        webView.loadUrl(s);
    }

}
