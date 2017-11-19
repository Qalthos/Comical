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

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebViewClient;

import com.linkybook.comical.R;
import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.data.SiteInfo;

import java.util.Date;

import static com.linkybook.comical.Utils.urlDomain;

public class WebView extends AppCompatActivity {
    private SiteViewModel svm;
    private ShareActionProvider share;
    private SiteInfo currentSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        svm = ViewModelProviders.of(this).get(SiteViewModel.class);

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
                    currentSite.lastVisit = new Date();
                    currentSite.visits++;
                    WebView.this.svm.addOrUpdateSite(currentSite);
                    loadUrl();
                } else {
                    // Let the browser handle it.
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);

        if(WebView.this.currentSite.favorite == true) {
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
                SiteInfo site = WebView.this.currentSite;
                site.favorite = !site.favorite;
                if(site.favorite == true) {
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                } else {
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                }
                WebView.this.svm.addOrUpdateSite(site);
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
