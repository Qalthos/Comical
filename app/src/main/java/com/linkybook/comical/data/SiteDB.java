package com.linkybook.comical.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {SiteInfo.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class SiteDB extends RoomDatabase {
    private static SiteDB INSTANCE;

    public abstract SiteDao siteDao();

    public static SiteDB getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SiteDB.class, "site")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
