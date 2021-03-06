/*
 * Comical: An Android webcomic manager
 * Copyright (C) 2017  Nathaniel Case
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.linkybook.comical.views;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.linkybook.comical.R;
import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.data.SiteInfo;

import java.util.Date;

import static com.linkybook.comical.Utils.urlDomain;

public class WebViewActivity extends AppCompatActivity {
    private SiteViewModel svm;
    private ShareActionProvider share;
    private SiteInfo currentSite;

    private ProgressBar progressBar;
    private SwipeRefreshLayout srl;
    private WebView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        progressBar = findViewById(R.id.bar);
        srl = findViewById(R.id.swipe_refresh);
        mainView = findViewById(R.id.main_view);

        svm = ViewModelProviders.of(this).get(SiteViewModel.class);

        currentSite = getIntent().getParcelableExtra("site");
        getSupportActionBar().setTitle(currentSite.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //WebSettings webSettings = mainView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        mainView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                String currentDomain = urlDomain(currentSite.url);
                String prospectDomain = urlDomain(url);
                if(prospectDomain.equals(currentDomain)) {
                    currentSite.url = url;
                    currentSite.favicon = view.getFavicon();
                    currentSite.lastVisit = new Date();
                    currentSite.visits++;
                    WebViewActivity.this.svm.addOrUpdateSite(currentSite);
                    loadUrl();
                } else {
                    // Let the browser handle it.
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }

                return true;
            }

            public void onPageFinished(WebView view, String url) {
                // Clear refresh spinner if necessary
                srl.setRefreshing(false);
                getSupportActionBar().setTitle(view.getTitle());
            }
        });

        mainView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(android.webkit.WebView view, int progress) {
                progressBar.setProgress(progress);

                if(progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                } else if(progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainView.reload();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);

        if(WebViewActivity.this.currentSite.favorite == true) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_favorite_black_24dp);
        }

        MenuItem shareItem = menu.findItem(R.id.action_share);
        share = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        loadUrl();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                SiteInfo site = WebViewActivity.this.currentSite;
                site.favorite = !site.favorite;
                if(site.favorite == true) {
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                } else {
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                }
                WebViewActivity.this.svm.addOrUpdateSite(site);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

        // Update share intent
        if(share != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentSite.url);
            share.setShareIntent(shareIntent);
        }
    }
}
