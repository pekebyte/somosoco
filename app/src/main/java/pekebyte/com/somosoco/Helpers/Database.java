package pekebyte.com.somosoco.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.google.gson.Gson;

import pekebyte.com.somosoco.Model.Item;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by pedromolina on 2/7/18.
 */

public class Database {

    public void createDB(Context mContext){
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        ocoDB.execSQL("CREATE TABLE IF NOT EXISTS ocoposts (published DATETIME, item TEXT)");
        ocoDB.close();
    }

    public Boolean checkIfExists(Context mContext,Item post){
        Gson gson = new Gson();
        String json = gson.toJson(post);
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        String rawSQL = "SELECT * FROM ocoposts WHERE item= ?";
        Cursor c = ocoDB.rawQuery(rawSQL, new String[]{json});
        Boolean response = false;
        if (c.getCount() > 0){
            response = true;
        }
        ocoDB.close();
        return response;
    }

    public void insertPost(Context mContext, Item post){
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        Gson gson = new Gson();
        String json = gson.toJson(post);
        String sql = "INSERT INTO ocoposts (published, item) VALUES (?, ?)";
        SQLiteStatement statement = ocoDB.compileStatement(sql);

        statement.bindString(1, post.getPublished().toString());
        statement.bindString(2, json);

        statement.execute();
        statement.close();

        ocoDB.close();
    }
}
