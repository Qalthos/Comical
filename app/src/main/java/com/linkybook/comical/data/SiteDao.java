package com.linkybook.comical.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

@Dao
public interface SiteDao {
    @Query("SELECT * FROM site")
    LiveData<List<SiteInfo>> getAll();

    @Query("SELECT * FROM site WHERE name LIKE :name LIMIT 1")
    SiteInfo findSiteByName(String name);

    @Query("SELECT * FROM site WHERE id LIKE :id LIMIT 1")
    SiteInfo findSiteById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SiteInfo... sites);

    @Update
    void updateSites(SiteInfo... sites);

    @Delete
    void delete(SiteInfo site);

}
