package com.app.history.medical;



import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class TCActivity extends Activity {
	WebView web;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tc);
        web = (WebView) findViewById(R.id.webView1);
        WebView wv= (WebView)findViewById(R.id.webView1);
        wv.loadUrl("file:///android_asset/termcon.html");
        wv.getSettings().setJavaScriptEnabled(true);
    }

}