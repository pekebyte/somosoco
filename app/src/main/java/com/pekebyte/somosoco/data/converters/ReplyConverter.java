package com.pekebyte.somosoco.data.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.pekebyte.somosoco.data.models.Replies;

public class ReplyConverter {

    @TypeConverter
    public static Replies fromString(String value) {
        return new Gson().fromJson(value, Replies.class);
    }

    @TypeConverter
    public static String fromReplies(Replies replies) {
        Gson gson = new Gson();
        String json = gson.toJson(replies);
        return json;
    }
}