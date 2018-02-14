package pekebyte.com.somosoco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.ArrayList;

import pekebyte.com.somosoco.Adapters.PostAdapter;
import pekebyte.com.somosoco.Helpers.API;
import pekebyte.com.somosoco.Helpers.Constants;
import pekebyte.com.somosoco.Helpers.Database;
import pekebyte.com.somosoco.Helpers.RestAdapter;
import pekebyte.com.somosoco.Model.Item;
import pekebyte.com.somosoco.Model.OcoPosts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {
    ArrayList<Item> postList = new ArrayList<Item>();
    PostAdapter pa;
    String prevPageToken = "";
    Context mContext;
    API api;
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


        api = RestAdapter.createAPI();
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
                Call<OcoPosts> callbackCall = api.getPosts(Constants.BLOGGER_KEY, null);

                callbackCall.enqueue(new Callback<OcoPosts>() {
                    @Override
                    public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                        if (response.isSuccessful()){
                            if (response.body().getItems().size() > 0){
                                for (int i=0; i<response.body().getItems().size(); i++){
                                    Item item = response.body().getItems().get(i);
                                    if (!db.checkIfExists(mContext,response.body().getItems().get(i))){
                                        db.insertPost(mContext,response.body().getItems().get(i));
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
            callbackCall = api.getPosts(Constants.BLOGGER_KEY, token);
        }
        else{
            callbackCall = api.getPosts(Constants.BLOGGER_KEY, null);
        }
        callbackCall.enqueue(new Callback<OcoPosts>() {
            @Override
            public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                if (response.isSuccessful()){
                    if (response.body().getItems().size() > 0){
                        for (int i=0; i<response.body().getItems().size(); i++){
                            if (!db.checkIfExists(mContext,response.body().getItems().get(i))){
                                db.insertPost(mContext,response.body().getItems().get(i));
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
        int itemIndex = c.getColumnIndex("item");
        Gson gson = new Gson();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String json = c.getString(itemIndex);
            Item item = gson.fromJson(json, Item.class);
            postList.add(item);
            c.moveToNext();
        }
        c.close();
        ocoDB.close();
        pa.notifyDataSetChanged();
    }
}
