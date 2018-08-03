package com.pekebyte.somosoco.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.network.Webservice;
import com.pekebyte.somosoco.ui.helpers.Constants;

import java.io.IOException;
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
        Log.d("start networking", "holi");
        webservice.getPosts(Constants.BLOGGER_KEY,token).enqueue(new Callback<OcoPosts>() {
            @Override
            public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                    Log.d("Finally", "okurrr");
                }else{
                    try {
                        Log.d("error networking", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OcoPosts> call, Throwable t) {
                Log.d("error networking", t.getMessage());
            }
        });

        return data;
    }

}
