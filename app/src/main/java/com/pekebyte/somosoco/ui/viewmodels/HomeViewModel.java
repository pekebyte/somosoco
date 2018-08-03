package com.pekebyte.somosoco.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.pekebyte.somosoco.data.AppDatabase;
import com.pekebyte.somosoco.data.dao.PostDao;
import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.repository.PostRepository;

import java.util.List;
import java.util.concurrent.Executor;

import static android.content.Context.MODE_PRIVATE;

public class HomeViewModel extends ViewModel {

    private PostRepository postRepo;
    private String token;
    private SharedPreferences sharedPreferences;
    private PostDao dao;
    private Context ctx;
    private Executor executor;

    public void init(Context ctx, PostRepository postRepo, Executor executor) {
        this.postRepo = postRepo;
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("com.pekebyte.somosoco", MODE_PRIVATE);
        this.token = sharedPreferences.getString("token",null);
        this.dao = AppDatabase.getDatabase(ctx).postDao();
        this.executor = executor;
    }

    public LiveData<List<Post>> getPosts(Boolean latest){
        final MutableLiveData<List<Post>> data = new MutableLiveData<>();

        String reqToken =  null;
        if (latest == false) {
               reqToken = this.token;
        }
        LiveData<List<Post>> response = this.postRepo.getPosts(reqToken);



        return data;
    }

    private void retrievePosts(Boolean latest){

        this.postRepo.getPosts(null).
    }

//    public LiveData<List<Post>> getPosts(Boolean latest) {
//        String reqToken =  null;
//        if (latest == false) {
//               reqToken = this.token;
//        }
//        LiveData<List<Posts>> response = this.postRepo.getPosts(reqToken).getValue().getPosts();
//
//        if (response != null) {
//
//            response.observe(ctx, new Observer<OcoPosts>() {
//                @Override
//                public void onChanged(@Nullable OcoPosts ocoPosts) {
//
//                }
//            });
//            if (response.getValue().getNextPageToken() != null) {
//                this.token = response.getValue().getNextPageToken();
//                sharedPreferences.edit().putString("token", this.token).apply();
//            }
//
//
//            for (int i = 0; i <= response.getValue().getPosts().size(); i++) {
//                Post post = response.getValue().getPosts().get(i);
//                dao.save(post);
//            }
//        }
//
//        return dao.getAllPosts();
//    }

}
