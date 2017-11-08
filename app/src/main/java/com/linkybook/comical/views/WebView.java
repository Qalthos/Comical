package com.linkybook.comical.views;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.linkybook.comical.R;
import com.linkybook.comical.data.SiteDB;
import com.linkybook.comical.data.SiteDao;
import com.linkybook.comical.data.SiteInfo;

import static com.linkybook.comical.Utils.urlDomain;

public class WebView extends AppCompatActivity {
    public static SiteInfo currentSite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        final SiteDao siteDB = SiteDB.getAppDatabase(this).siteDao();
        int site_id = getIntent().getIntExtra("id", -1);
        currentSite = siteDB.findSiteById(site_id);

        getSupportActionBar().setTitle(currentSite.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

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
        if(currentSite.favicon != null) {
            Drawable d = new BitmapDrawable(getResources(), currentSite.favicon);
            getSupportActionBar().setIcon(d);
        }
        getSupportActionBar().setSubtitle(currentSite.url);
        android.webkit.WebView mainView = (android.webkit.WebView) findViewById(R.id.main_view);
        mainView.loadUrl(currentSite.url);

    }
}
