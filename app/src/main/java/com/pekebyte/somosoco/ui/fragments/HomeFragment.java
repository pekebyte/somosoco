package com.pekebyte.somosoco.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.pekebyte.somosoco.R;
import com.pekebyte.somosoco.data.models.OcoPosts;
import com.pekebyte.somosoco.data.repository.PostRepository;
import com.pekebyte.somosoco.ui.adapters.PostAdapter;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.data.network.Webservice;
import com.pekebyte.somosoco.data.network.RestAdapter;
import com.pekebyte.somosoco.ui.viewmodels.HomeViewModel;


public class HomeFragment extends Fragment {
    List<Post> postList;
    PostAdapter pa;
    Context mContext;
    ListView lv;
    SwipeRefreshLayout sr;
    Webservice webservice;
    HomeViewModel viewModel;

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

        //Webservice
        webservice = RestAdapter.createAPI();

        //ViewModel
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.init(mContext,new PostRepository(webservice));


        //Listview
         lv = (ListView) v.findViewById(R.id.postlist);
        lv.setDivider(null);
        postList = new ArrayList<>();
        pa = new PostAdapter(mContext, postList, true);

        lv.setAdapter(pa);


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getAdapter().getCount() > 0) {
                    if (view.getLastVisiblePosition() == view.getAdapter().getCount() - 1 && view.getChildAt(view.getChildCount() - 1).getBottom() <= view.getHeight()) {
                        sendRequest(false);
                    }
                }
            }
        });



        //Swipe refresh

        sr = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest(true);
            }
        });

        sr.post(new Runnable() {
            @Override
            public void run() {
                sr.setRefreshing(true);
                sendRequest(true);
            }
        });

        return v;
    }

    public void sendRequest(Boolean latest)
    {
        Log.d("sendRequest", latest.toString());
        Observer<List<Post>> observer = new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                if (posts.size() > 0) {
                    updatePosts(posts);
                }
                sr.setRefreshing(false);
                viewModel.getPosts(latest).removeObserver(this);
            }
        };

        viewModel.getPosts(latest).observe(this, observer);

    }

    public void updatePosts(List<Post> posts){
        this.postList.clear();
        this.postList.addAll(posts);
        pa.notifyDataSetChanged();
    }



}
