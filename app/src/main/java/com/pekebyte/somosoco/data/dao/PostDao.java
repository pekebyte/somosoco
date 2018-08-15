package com.pekebyte.somosoco.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.pekebyte.somosoco.data.models.Post;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PostDao {
    @Insert(onConflict = REPLACE)
    void save(Post post);

    @Query("SELECT * FROM posts WHERE id = :postId")
    LiveData<Post> loadPost(String postId);

    //Get All Users
    @Query("SELECT * FROM posts ORDER BY updated DESC")
    LiveData<List<Post>> getAllPosts();

    @Query("SELECT COUNT(id) FROM posts WHERE id = :postId")
    int exists(String postId);

}
