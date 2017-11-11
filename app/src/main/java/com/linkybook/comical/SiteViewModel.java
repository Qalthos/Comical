package com.linkybook.comical;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.linkybook.comical.data.SiteDB;
import com.linkybook.comical.data.SiteInfo;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {
    private final LiveData<List<SiteInfo>> siteList;
    private SiteDB siteDB;

    public SiteViewModel(Application application) {
        super(application);
        siteDB = SiteDB.getAppDatabase(this.getApplication());
        siteList = siteDB.siteDao().getAll();
    }

    public LiveData<List<SiteInfo>> getSiteList() {
        return siteList;
    }

    public void addSite(final SiteInfo site) {
        new addAsyncTask(siteDB).execute(site);
    }

    public void deleteSite(SiteInfo site) {
        new deleteAsyncTask(siteDB).execute(site);
    }

    private static class addAsyncTask extends AsyncTask<SiteInfo, Void, Void> {
        private SiteDB db;

        addAsyncTask(SiteDB appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final SiteInfo... params) {
            db.siteDao().insertAll(params);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<SiteInfo, Void, Void> {
        private SiteDB db;

        deleteAsyncTask(SiteDB appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final SiteInfo... params) {
            db.siteDao().delete(params[0]);
            return null;
        }

    }
}