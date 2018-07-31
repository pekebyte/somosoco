package com.pekebyte.somosoco.data.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;

import java.sql.Timestamp;

public class TimestampConverter {
    @TypeConverter
    public static Timestamp fromString(String value) {
        return new Gson().fromJson(value, Timestamp.class);
    }

    @TypeConverter
    public static String fromTimestamp(Timestamp published) {
        Gson gson = new Gson();
        String json = gson.toJson(published);
        return json;
    }
}
