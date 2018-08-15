package com.pekebyte.somosoco.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.pekebyte.somosoco.data.models.Favorite;
import com.pekebyte.somosoco.data.models.Post;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = REPLACE)
    void save(Favorite favorite);

    @Delete
    void delete(Favorite favorite);

    @Query("SELECT posts.* FROM posts,favorites WHERE posts.id = favorites.id ORDER BY posts.updated DESC")
    LiveData<List<Post>> getFavorites();

    @Query("SELECT COUNT(id) FROM favorites WHERE id = :postId")
    int existInFavorite(String postId);
}
