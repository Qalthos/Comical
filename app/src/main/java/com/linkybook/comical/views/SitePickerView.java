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

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.linkybook.comical.R;
import com.linkybook.comical.RecyclerViewAdapter;
import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.Utils;
import com.linkybook.comical.data.SiteInfo;

import java.util.ArrayList;
import java.util.List;

public class SitePickerView extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener {
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_picker);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SitePickerView.this, SiteEditor.class));
            }
        });

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<SiteInfo>(), this);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.setAdapter(recyclerViewAdapter);

        SiteViewModel svm = ViewModelProviders.of(this).get(SiteViewModel.class);
        svm.getSiteList().observe(SitePickerView.this, new Observer<List<SiteInfo>>() {
            @Override
            public void onChanged(@Nullable List<SiteInfo> siteInfos) {
                recyclerViewAdapter.addItems(siteInfos);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                Utils.importFromFile(SitePickerView.this);
                break;
            case R.id.action_export:
                Utils.exportToFile(SitePickerView.this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        SiteInfo site = (SiteInfo) v.getTag();
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("site", site);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        SiteInfo site = (SiteInfo) v.getTag();
        Intent intent = new Intent(this, SiteEditor.class);
        intent.putExtra("site", site);
        startActivity(intent);
        return true;
    }
}
