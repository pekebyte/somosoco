package com.pekebyte.somosoco.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.pekebyte.somosoco.data.dao.PostDao;
import com.pekebyte.somosoco.data.models.Post;

@Database(entities = {Post.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "somosoco_db")
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public abstract PostDao postDao();
}
