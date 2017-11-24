package com.linkybook.comical.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {SiteInfo.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class SiteDB extends RoomDatabase {
    private static SiteDB INSTANCE;

    public abstract SiteDao siteDao();

    public static SiteDB getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SiteDB.class, "site")
                            .addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `site` ADD visits INTEGER NOT NULL default 0");
            database.execSQL("ALTER TABLE `site` ADD last_visit INTEGER default 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `site` ADD favorite INTEGER NOT NULL default 0");
        }
    };
}