package com.pekebyte.somosoco.ui.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;

import com.pekebyte.somosoco.data.AppDatabase;
import com.pekebyte.somosoco.data.dao.PostDao;
import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ServiceViewModel extends ViewModel {

    private PostRepository postRepo;
    private String token;
    private PostDao dao;
    private Context ctx;
    public void init(Context ctx, PostRepository postRepo) {
        this.postRepo = postRepo;
        this.ctx = ctx;
        this.dao = AppDatabase.getDatabase(ctx).postDao();
    }

    public LiveData<List<Post>> getPosts(){

        LiveData<OcoPosts> response = this.postRepo.getPosts(null);


        LiveData<List<Post>> data = Transformations.switchMap(response, res -> {
            List<Post> notSaved = new ArrayList<>();
            MutableLiveData<List<Post>> notifyPosts = new MutableLiveData<>();
            for (int i =0; i<res.getPosts().size(); i++){
                Post post = res.getPosts().get(i);
                if (dao.exists(post.getId()) == 0){
                    notSaved.add(post);
                    dao.save(post);
                }
            }
            notifyPosts.setValue(notSaved);
            return notifyPosts;
        });

        return data;
    }

}
