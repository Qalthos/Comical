package com.linkybook.comical.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.linkybook.comical.R;
import com.linkybook.comical.RecyclerViewAdapter;
import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.data.SiteInfo;

import java.util.ArrayList;
import java.util.List;

public class SitePickerView extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener {
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.site_picker);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        SiteInfo site = (SiteInfo) v.getTag();
        Intent intent = new Intent(this, WebView.class);
        intent.putExtra("id", site.id);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        SiteInfo site = (SiteInfo) v.getTag();
        Intent intent = new Intent(this, SiteEditor.class);
        intent.putExtra("id", site.id);
        startActivity(intent);
        return true;
    }
}
