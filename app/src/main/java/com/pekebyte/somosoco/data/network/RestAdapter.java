package com.pekebyte.somosoco.data.network;

import com.pekebyte.somosoco.ui.helpers.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pedromolina on 2/4/18.
 */

public class RestAdapter {

    static Retrofit retrofit;
    static OkHttpClient okHttpClient;

    public static Webservice createAPI() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.cache(null);
        okHttpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(Webservice.class);
    }

    public static void cancel(){
        okHttpClient.dispatcher().cancelAll();
    }

    public static Retrofit getRetrofit(){
        return retrofit;
    }
}
