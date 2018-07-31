package com.pekebyte.somosoco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.ArrayList;

import com.pekebyte.somosoco.adapters.PostAdapter;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.network.Webservice;
import com.pekebyte.somosoco.helpers.Constants;
import com.pekebyte.somosoco.helpers.Database;
import com.pekebyte.somosoco.data.network.RestAdapter;
import com.pekebyte.somosoco.data.models.OcoPosts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {
    ArrayList<Post> postList = new ArrayList<Post>();
    PostAdapter pa;
    String prevPageToken = "";
    Context mContext;
    Webservice webservice;
    String token;
    SwipeRefreshLayout sr;

    ProgressBar progressBar;

    SharedPreferences sharedPreferences;

    Database db;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Context
        mContext = container.getContext();

        //Listview
        ListView lv = (ListView) v.findViewById(R.id.postlist);
        lv.setDivider(null);

        sharedPreferences = mContext.getSharedPreferences("pekebyte.com.somosoco", MODE_PRIVATE);
        db = new Database();
        db.createDB(mContext);


        webservice = RestAdapter.createAPI();
        token = sharedPreferences.getString("token",null);

        pa = new PostAdapter(mContext, postList, true);

        lv.setAdapter(pa);


        //Start Background service
        Intent service = new Intent(mContext, PostService.class);
        mContext.startService(service);

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getAdapter().getCount() > 0){
                    if (view.getLastVisiblePosition() == view.getAdapter().getCount() - 1 && view.getChildAt(view.getChildCount() - 1).getBottom() <= view.getHeight()) {
                        retrievePosts(false);
                    }
                }
            }
        });

        //Swipe refresh

        sr = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Call<OcoPosts> callbackCall = webservice.getPosts(Constants.BLOGGER_KEY, null);

                callbackCall.enqueue(new Callback<OcoPosts>() {
                    @Override
                    public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                        if (response.isSuccessful()){
                            if (response.body().getPosts().size() > 0){
                                for (int i = 0; i<response.body().getPosts().size(); i++){
                                    Post post = response.body().getPosts().get(i);
                                    if (!db.checkIfExists(mContext,response.body().getPosts().get(i))){
                                        db.insertPost(mContext,response.body().getPosts().get(i));
                                    }
                                }
                            }
                        }
                        getAllPosts();
                        sr.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<OcoPosts> call, Throwable t) {
                        getAllPosts();
                        sr.setRefreshing(false);
                    }
                });
            }
        });

        sr.post(new Runnable() {
            @Override
            public void run() {
                sr.setRefreshing(true);
                getAllPosts();
                retrievePosts(true);
            }
        });

        return v;
    }


    private void retrievePosts(Boolean checkLatests){
        Call<OcoPosts> callbackCall = null;
        if (checkLatests == false) {
            callbackCall = webservice.getPosts(Constants.BLOGGER_KEY, token);
        }
        else{
            callbackCall = webservice.getPosts(Constants.BLOGGER_KEY, null);
        }
        callbackCall.enqueue(new Callback<OcoPosts>() {
            @Override
            public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                if (response.isSuccessful()){
                    if (response.body().getPosts().size() > 0){
                        for (int i = 0; i<response.body().getPosts().size(); i++){
                            if (!db.checkIfExists(mContext,response.body().getPosts().get(i))){
                                db.insertPost(mContext,response.body().getPosts().get(i));
                            }
                        }
                        token = response.body().getNextPageToken();
                        sharedPreferences.edit().putString("token",token).apply();
                    }
                }
                getAllPosts();
                if (sr.isRefreshing()){
                    sr.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<OcoPosts> call, Throwable t) {
                getAllPosts();
                if (sr.isRefreshing()){
                    sr.setRefreshing(false);
                }
            }
        });
    }




    private void getAllPosts(){
        postList.clear();
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        Cursor c = ocoDB.rawQuery("SELECT * FROM ocoposts ORDER BY published DESC", null);
        int itemIndex = c.getColumnIndex("post");
        Gson gson = new Gson();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String json = c.getString(itemIndex);
            Post post = gson.fromJson(json, Post.class);
            postList.add(post);
            c.moveToNext();
        }
        c.close();
        ocoDB.close();
        pa.notifyDataSetChanged();
    }
}
