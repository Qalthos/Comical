package com.linkybook.comical.views;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebViewClient;

import com.linkybook.comical.R;
import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.data.SiteInfo;

import static com.linkybook.comical.Utils.urlDomain;

public class WebView extends AppCompatActivity {
    public static SiteInfo currentSite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        currentSite = getIntent().getParcelableExtra("site");
        getSupportActionBar().setTitle(currentSite.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        android.webkit.WebView mainView = (android.webkit.WebView) findViewById(R.id.main_view);
        //WebSettings webSettings = mainView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        mainView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                String currentDomain = urlDomain(currentSite.url);
                String prospectDomain = urlDomain(url);
                if(prospectDomain.equals(currentDomain)) {
                    currentSite.url = url;
                    currentSite.favicon = view.getFavicon();
                    siteDB.updateSites(currentSite);
                    loadUrl();
                    return true;
                } else {
                    return false;
                }
            }
        });

        loadUrl();
    }

    @Override
    public void onBackPressed() {
        android.webkit.WebView browser = (android.webkit.WebView) findViewById(R.id.main_view);
        if(browser.canGoBack()) {
            browser.goBack();
        } else {
            goHome();
        }
    }

    private void goHome() {
        super.onBackPressed();
    }

    private void loadUrl() {
        getSupportActionBar().setSubtitle(currentSite.url);
        android.webkit.WebView mainView = (android.webkit.WebView) findViewById(R.id.main_view);
        mainView.loadUrl(currentSite.url);

    }
}
