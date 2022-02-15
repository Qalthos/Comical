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

import static com.linkybook.comical.utils.URL.urlDomain;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.linkybook.comical.R;
import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.data.SiteInfo;
import com.linkybook.comical.utils.Orientation;

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

        svm = new ViewModelProvider(this).get(SiteViewModel.class);

        currentSite = getIntent().getParcelableExtra("site");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setTitle(currentSite.name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (currentSite.orientation == Orientation.LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        } else if (currentSite.orientation == Orientation.PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        mainView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        mainView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, WebResourceRequest wrq) {
                String currentDomain = urlDomain(currentSite.url);
                String prospectDomain = urlDomain(wrq.getUrl());
                if(prospectDomain.equals(currentDomain)) {
                    currentSite.url = wrq.getUrl().toString();
                    currentSite.favicon = view.getFavicon();
                    currentSite.visit();
                    WebViewActivity.this.svm.addOrUpdateSite(currentSite);
                    loadUrl();
                } else {
                    // Let the browser handle it.
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(wrq.getUrl().toString())));
                }

                return true;
            }

            public void onPageFinished(WebView view, String url) {
                // Clear refresh spinner if necessary
                srl.setRefreshing(false);
                if (actionBar != null) {
                    actionBar.setTitle(view.getTitle());
                }
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

        srl.setOnRefreshListener(() -> mainView.reload());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);

        if(WebViewActivity.this.currentSite.favorite) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_favorite_black_24dp);
        }
        menu.findItem(R.id.action_backlog).setVisible(WebViewActivity.this.currentSite.backlog);
        menu.findItem(R.id.action_hiatus).setVisible(WebViewActivity.this.currentSite.hiatus);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        share = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        loadUrl();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SiteInfo site = WebViewActivity.this.currentSite;
        if (item.getItemId() == R.id.action_favorite) {
            site.favorite = !site.favorite;
            if (site.favorite) {
                item.setIcon(R.drawable.ic_favorite_black_24dp);
            } else {
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }
        } else if (item.getItemId() == R.id.action_backlog) {
            site.backlog = false;
            item.setVisible(false);
        } else if (item.getItemId() == R.id.action_hiatus) {
            site.hiatus = false;
            item.setVisible(false);
        } else {
            return super.onOptionsItemSelected(item);
        }
        WebViewActivity.this.svm.addOrUpdateSite(site);
        return true;
    }

    @Override
    public void onBackPressed() {
        android.webkit.WebView browser = findViewById(R.id.main_view);
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(currentSite.url);
        }
        android.webkit.WebView mainView = findViewById(R.id.main_view);
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
