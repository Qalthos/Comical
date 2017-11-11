package com.linkybook.comical.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import static com.linkybook.comical.data.Converters.base64ToBitmap;
import static com.linkybook.comical.data.Converters.bitmapToBase64;

@Entity(tableName = "site",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class SiteInfo implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public String url;

    public Bitmap favicon;

    public SiteInfo(String name, String url, Bitmap favicon) {
        this.name = name;
        this.url = url;
        this.favicon = favicon;
    }

    @Ignore
    public SiteInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        String b64Icon = bitmapToBase64(favicon);
        String[] data = {name, url, b64Icon);
        out.writeStringArray(data);
    }

    public static final Parcelable.Creator<SiteInfo> CREATOR = new Parcelable.Creator<SiteInfo>() {
        public SiteInfo createFromParcel(Parcel in) {
            String[] data = in.createStringArray();
            return new SiteInfo(data[0], data[1], base64ToBitmap(data[2]));
        }

        public SiteInfo[] newArray(int size) {
            return new SiteInfo[size];
        }
    };

}
