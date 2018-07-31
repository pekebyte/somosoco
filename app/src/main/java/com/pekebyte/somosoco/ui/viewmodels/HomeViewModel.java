package com.pekebyte.somosoco.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;

import com.pekebyte.somosoco.data.AppDatabase;
import com.pekebyte.somosoco.data.dao.PostDao;
import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.repository.PostRepository;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeViewModel extends ViewModel {

    private PostRepository postRepo;
    private String token;
    private SharedPreferences sharedPreferences;
    private PostDao dao;

    public void init(Context ctx, PostRepository postRepo) {
        this.postRepo = postRepo;
        sharedPreferences = ctx.getSharedPreferences("com.pekebyte.somosoco", MODE_PRIVATE);
        this.token = sharedPreferences.getString("token",null);
        this.dao = AppDatabase.getDatabase(ctx).postDao();
    }

    public LiveData<List<Post>> getPosts(Boolean latest) {
        String reqToken =  null;
        if (latest == false) {
               reqToken = this.token;
        }
        LiveData<OcoPosts> response = this.postRepo.getPosts(reqToken);
        this.token = response.getValue().getNextPageToken();
        sharedPreferences.edit().putString("token",this.token).apply();

        for (int i = 0; i<= response.getValue().getPosts().size(); i++){
            Post post = response.getValue().getPosts().get(i);
            dao.save(post);
        }

        return dao.getAllPosts();
    }
}
