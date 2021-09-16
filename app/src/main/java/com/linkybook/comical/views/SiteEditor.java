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

import static com.linkybook.comical.utils.Schedule.decodeUpdates;
import static com.linkybook.comical.utils.Schedule.encodeUpdates;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.linkybook.comical.R;
import com.linkybook.comical.SiteViewModel;
import com.linkybook.comical.data.SiteInfo;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SiteEditor extends AppCompatActivity {
    private SiteViewModel svm;
    private SiteInfo site;

    EditText name;
    EditText url;
    MaterialButtonToggleGroup toggleGroup;
    final Map<Integer, DayOfWeek> days = new HashMap<Integer, DayOfWeek>() {
        {
            put(R.id.dow_mon, DayOfWeek.MONDAY);
            put(R.id.dow_tue, DayOfWeek.TUESDAY);
            put(R.id.dow_wed, DayOfWeek.WEDNESDAY);
            put(R.id.dow_thu, DayOfWeek.THURSDAY);
            put(R.id.dow_fri, DayOfWeek.FRIDAY);
            put(R.id.dow_sat, DayOfWeek.SATURDAY);
            put(R.id.dow_sun, DayOfWeek.SUNDAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_editor);
        setupActionBar();

        name = findViewById(R.id.site_name);
        url = findViewById(R.id.site_url);
        toggleGroup = findViewById(R.id.site_update_picker);
        toggleGroup.clearChecked();
        Button submitButton = findViewById(R.id.site_add_button);

        svm = new ViewModelProvider(this).get(SiteViewModel.class);
        site = getIntent().getParcelableExtra("site");

        if (site == null) {
            site = new SiteInfo();
        } else {
            name.setText(site.name);
            url.setText(site.url);
            if (site.visits > 0) {
                ArrayList<DayOfWeek> selectedDays = decodeUpdates(site.visits);
                for (Map.Entry<Integer, DayOfWeek> dow : days.entrySet()) {
                    if (selectedDays.contains(dow.getValue())) {
                        toggleGroup.check(dow.getKey());
                    }
                }
            }
            submitButton.setText(R.string.prompt_update);
        }

        submitButton.setOnClickListener(view -> {
            site.name = name.getText().toString();
            site.url = url.getText().toString();
            ArrayList<DayOfWeek> selectedDays = new ArrayList<>();
            for (int id : toggleGroup.getCheckedButtonIds()) {
                selectedDays.add(days.get(id));
            }
            site.visits = encodeUpdates(selectedDays);
            svm.addOrUpdateSite(site);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        if (site.favorite) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_favorite_black_24dp);
        }
        if (site.name != null) {
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SiteInfo site = SiteEditor.this.site;

        if (item.getItemId() == R.id.action_favorite) {
            site.favorite = !site.favorite;
            if (site.favorite) {
                item.setIcon(R.drawable.ic_favorite_black_24dp);
            } else {
                item.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }
        } else if (item.getItemId() == R.id.action_delete) {
            svm.deleteSite(site);
            finish();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
