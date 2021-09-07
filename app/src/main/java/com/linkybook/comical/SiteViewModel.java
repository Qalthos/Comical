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

package com.linkybook.comical;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.linkybook.comical.data.SiteDB;
import com.linkybook.comical.data.SiteInfo;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {
    private final LiveData<List<SiteInfo>> siteList;
    private final SiteDB siteDB;

    public SiteViewModel(Application application) {
        super(application);
        siteDB = SiteDB.getAppDatabase(this.getApplication());
        siteList = siteDB.siteDao().getAll();
    }

    public LiveData<List<SiteInfo>> getSiteList() {
        return siteList;
    }

    public String exportToJson() {
        Gson gson = new Gson();
        return gson.toJson(siteList.getValue());
    }

    public void importFromJson(String json) {
        Gson gson = new Gson();
        addOrUpdateSite(gson.fromJson(json, SiteInfo[].class));
    }

    public void addOrUpdateSite(final SiteInfo... sites) {
        AsyncTask.execute(() -> siteDB.siteDao().insertAll(sites));
    }

    public void deleteSite(SiteInfo site) {
        AsyncTask.execute(() -> siteDB.siteDao().delete(site));
    }
}