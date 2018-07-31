package com.pekebyte.somosoco.data.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.pekebyte.somosoco.data.models.Blog;

public class BlogConverter {
    @TypeConverter
    public static Blog fromString(String value) {
        return new Gson().fromJson(value, Blog.class);
    }

    @TypeConverter
    public static String fromBlog(Blog blog) {
        Gson gson = new Gson();
        String json = gson.toJson(blog);
        return json;
    }
}
