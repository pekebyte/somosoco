package pekebyte.com.somosoco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.Drawable;
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

import pekebyte.com.somosoco.Adapters.PostAdapter;
import pekebyte.com.somosoco.Helpers.API;
import pekebyte.com.somosoco.Helpers.Constants;
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

    ProgressBar progressBar;

    SharedPreferences sharedPreferences;
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
        createDB();


        api = RestAdapter.createAPI();
        token = sharedPreferences.getString("token",null);

        pa = new PostAdapter(mContext, postList);

        lv.setAdapter(pa);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        getAllPosts();
        retrievePosts();
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
                        retrievePosts();
                    }
                }
            }
        });

        //Swipe refresh

        final SwipeRefreshLayout sr = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

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
                                    if (!checkIfExists(response.body().getItems().get(i).getId())){
                                        insertPost(response.body().getItems().get(i));
                                    }
                                }
                                getAllPosts();
                                sr.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OcoPosts> call, Throwable t) {

                    }
                });
            }
        });

        return v;
    }


    private void retrievePosts(){
        Call<OcoPosts> callbackCall = api.getPosts(Constants.BLOGGER_KEY, token);

        callbackCall.enqueue(new Callback<OcoPosts>() {
            @Override
            public void onResponse(Call<OcoPosts> call, Response<OcoPosts> response) {
                if (response.isSuccessful()){
                    if (response.body().getItems().size() > 0){
                        for (int i=0; i<response.body().getItems().size(); i++){
                            if (!checkIfExists(response.body().getItems().get(i).getId())){
                                postList.add(response.body().getItems().get(i));
                                insertPost(response.body().getItems().get(i));
                            }
                        }
                        token = response.body().getNextPageToken();
                        sharedPreferences.edit().putString("token",token).apply();
                        pa.notifyDataSetChanged();
                    }
                }
                if (progressBar.getVisibility() == View.VISIBLE){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<OcoPosts> call, Throwable t) {
                if (progressBar.getVisibility() == View.VISIBLE){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void createDB(){
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        ocoDB.execSQL("CREATE TABLE IF NOT EXISTS ocoposts (id VARCHAR, item TEXT)");
        ocoDB.close();
    }

    private Boolean checkIfExists(Long id){
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        String rawSQL = "SELECT * FROM ocoposts WHERE id="+id;
        Cursor c = ocoDB.rawQuery(rawSQL, null);
        Boolean response = false;
        if (c.getCount() > 0){
            response = true;
        }
        ocoDB.close();
        return response;
    }

    private void getAllPosts(){
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        Cursor c = ocoDB.rawQuery("SELECT * FROM ocoposts", null);
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
        if (postList.size() > 0){
            progressBar.setVisibility(View.GONE);
        }
    }

    private void insertPost(Item post){
        SQLiteDatabase ocoDB = mContext.openOrCreateDatabase("somosoco", MODE_PRIVATE, null);
        Gson gson = new Gson();
        String json = gson.toJson(post);
        String sql = "INSERT INTO ocoposts (id, item) VALUES (?, ?)";
        SQLiteStatement statement = ocoDB.compileStatement(sql);

        statement.bindString(1, ""+post.getId());
        statement.bindString(2, json);

        statement.execute();
        statement.close();

        ocoDB.close();
    }
}
