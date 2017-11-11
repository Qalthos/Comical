package com.linkybook.comical.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.Date;

@Entity(tableName = "site",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class SiteInfo implements Parcelable, Comparable<SiteInfo> {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public String url;

    public Bitmap favicon;

    public int visits = 0;

    @ColumnInfo(name = "last_visit")
    public Date lastVisit = new Date(0);

    public SiteInfo() {
    }

    @Ignore
    public SiteInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public int compareTo(SiteInfo other) {
        return other.frecencyValue() - this.frecencyValue();
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
