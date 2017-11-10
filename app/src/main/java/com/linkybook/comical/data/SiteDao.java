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

package com.linkybook.comical.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
