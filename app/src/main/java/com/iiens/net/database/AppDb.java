package com.iiens.net.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.iiens.net.model.AnnivItem;
import com.iiens.net.model.NewsItem;

@Database(entities = {NewsItem.class, AnnivItem.class}, version = 1)
public abstract class AppDb extends RoomDatabase {

    public abstract NewsDao newsDao();
//  public abstract EdtSearchDao edtSearchDao();
    public abstract AnnivDao annivDao();
//  public abstract EdtItemDao edtItemDao();

    private static AppDb INSTANCE;

    public static AppDb getAppDb(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDb.class, "iiensDb")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
