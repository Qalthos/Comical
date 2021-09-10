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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.linkybook.comical.data.serializers.BitmapSerializer;
import com.linkybook.comical.data.serializers.LocalDateSerializer;

import java.util.Date;

@Entity(tableName = "site",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class SiteInfo implements Parcelable, Comparable<SiteInfo> {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public String url;

    @JsonAdapter(BitmapSerializer.class)
    public Bitmap favicon;

    public int visits = 0;

    @ColumnInfo(name = "last_visit")
    @JsonAdapter(LocalDateSerializer.class)
    public LocalDate lastVisit;

    public boolean favorite = false;

    @Override
    public int compareTo(SiteInfo other) {
        int favdiff = Boolean.compare(other.favorite, this.favorite);
        if(favdiff != 0) {
            return favdiff;
        } else {
            return other.frecencyValue() - this.frecencyValue();
        }
    }

    public int frecencyValue() {
        // Time difference in ms
        long timeDeltaMS = new Date().getTime() - this.lastVisit.getTime();
        // Convert ms to h
        int timeDeltaH = (int) Math.max(Math.ceil(timeDeltaMS / (1000 * 60 * 60)), 1d);
        return visits / timeDeltaH;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Gson gson = new Gson();
        out.writeString(gson.toJson(this));
    }

    public static final Parcelable.Creator<SiteInfo> CREATOR = new Parcelable.Creator<SiteInfo>() {
        public SiteInfo createFromParcel(Parcel in) {
            Gson gson = new Gson();
            return gson.fromJson(in.readString(), SiteInfo.class);
        }

        public SiteInfo[] newArray(int size) {
            return new SiteInfo[size];
        }
    };

}
