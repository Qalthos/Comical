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
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.R;
import com.linkybook.comical.data.SiteInfo;


public class SiteEditor extends AppCompatActivity {
    private SiteViewModel svm;
    private SiteInfo existingSite;

    EditText name;
    EditText url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_editor);
        setupActionBar();

        name = findViewById(R.id.site_name);
        url = findViewById(R.id.site_url);
        Button submitButton = findViewById(R.id.site_add_button);

        svm = ViewModelProviders.of(this).get(SiteViewModel.class);
        existingSite = getIntent().getParcelableExtra("site");

        if(existingSite == null) {
            // Only adding on new sites so existing url doesn't get clobbered
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    String name = editable.toString().toLowerCase().replaceAll("[^a-z]", "");
                    SiteEditor.this.url.setText("http://www." + name + ".com");
                }
            });
        } else {
            name.setText(site.name);
            url.setText(site.url);
            submitButton.setText(R.string.prompt_update);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SiteInfo site = existingSite == null ? new SiteInfo() : existingSite;
                site.name = name.getText().toString();
                site.url = url.getText().toString();
                svm.addOrUpdateSite(site);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        if(existingSite != null) {
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                svm.deleteSite(SiteEditor.this.existingSite);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
