package com.linkybook.comical.views;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.R;
import com.linkybook.comical.data.SiteDB;
import com.linkybook.comical.data.SiteInfo;


public class SiteEditor extends AppCompatActivity {
    private SiteViewModel svm;

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

        SiteInfo existingSite = getIntent().getParcelableExtra("site");
        if(existingSite != null) {
            name.setText(existingSite.name);
            url.setText(existingSite.url);
            submitButton.setText(R.string.prompt_update);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_s = name.getText().toString();
                String url_s = url.getText().toString();
                SiteInfo newSite = new SiteInfo(name_s, url_s);
                svm.addSite(newSite);
                finish();
            }
        });
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}