package com.pekebyte.somosoco.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.network.Webservice;
import com.pekebyte.somosoco.ui.helpers.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {
    private final Webservice webservice;

    public PostRepository(Webservice webservice) {
        this.webservice = webservice;
    }

    public LiveData<OcoPosts> getPosts(String token) {
        final MutableLiveData<OcoPosts> data = new MutableLiveData<>();

        webservice.getPosts(Constants.BLOGGER_KEY,token).enqueue(new Callback<OcoPosts>() {
            @Override
            public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<OcoPosts> call, Throwable t) {

            }
        });

        return data;
    }

}
