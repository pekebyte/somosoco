package com.pekebyte.somosoco.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.pekebyte.somosoco.data.AppDatabase;
import com.pekebyte.somosoco.data.dao.FavoriteDao;
import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;

import java.util.List;


public class SavedViewModel extends ViewModel {

    private FavoriteDao dao;
    private Context ctx;

    public void init(Context ctx) {
        this.ctx = ctx;
        this.dao = AppDatabase.getDatabase(ctx).favoriteDao();
    }

    public LiveData<List<Post>> getFavorites() {
        return this.dao.getFavorites();
    }
}
