package com.linkybook.comical.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import static com.linkybook.comical.data.Converters.base64ToBitmap;
import static com.linkybook.comical.data.Converters.bitmapToBase64;
import static com.linkybook.comical.data.Converters.dateToTimestamp;

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

    public SiteInfo(String name, String url, Bitmap favicon, int visits, Date lastVisit) {
        this.name = name;
        this.url = url;
        this.favicon = favicon;
        this.visits = visits;
        this.lastVisit = lastVisit;
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

    private int frecencyValue() {
        return (int) (visits * 10000 / (new Date().getTime() - this.lastVisit.getTime()));
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        String b64Icon = bitmapToBase64(favicon);
        String date = String.valueOf(dateToTimestamp(lastVisit));
        String[] data = {name, url, b64Icon, String.valueOf(visits), date};
        out.writeStringArray(data);
    }

    public static final Parcelable.Creator<SiteInfo> CREATOR = new Parcelable.Creator<SiteInfo>() {
        public SiteInfo createFromParcel(Parcel in) {
            String[] data = in.createStringArray();
            Date date = new Date(Long.parseLong(data[4]));
            return new SiteInfo(
                    data[0], data[1], base64ToBitmap(data[2]), Integer.parseInt(data[3]), date
            );
        }

        public SiteInfo[] newArray(int size) {
            return new SiteInfo[size];
        }
    };

}
