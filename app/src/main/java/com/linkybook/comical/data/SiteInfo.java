package com.linkybook.comical.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

@Entity(tableName = "site",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class SiteInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public String url;

    public Bitmap favicon;

    public String getDomain() {
        String[] urlBits = this.url.split("/");
        return urlBits[1];
    }
}
