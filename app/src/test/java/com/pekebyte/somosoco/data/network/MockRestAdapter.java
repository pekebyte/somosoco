package com.pekebyte.somosoco.data.network;

import com.pekebyte.somosoco.ui.helpers.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class MockRestAdapter {
    public Webservice build(){
        // Create a very simple Retrofit adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .build();

        // Create a MockRetrofit object with a NetworkBehavior which manages the fake behavior of calls.
        NetworkBehavior behavior = NetworkBehavior.create();
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();

        BehaviorDelegate<Webservice> delegate = mockRetrofit.create(Webservice.class);
        return new MockWebservice(delegate);
    }
}
