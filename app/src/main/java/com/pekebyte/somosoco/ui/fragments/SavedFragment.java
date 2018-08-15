package com.pekebyte.somosoco.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.pekebyte.somosoco.R;
import com.pekebyte.somosoco.ui.adapters.PostAdapter;
import com.pekebyte.somosoco.data.models.Post;
import com.pekebyte.somosoco.ui.viewmodels.HomeViewModel;
import com.pekebyte.somosoco.ui.viewmodels.SavedViewModel;

import static android.content.Context.MODE_PRIVATE;


public class SavedFragment extends Fragment {
    List<Post> postList;
    PostAdapter pa;
    Context mContext;
    SavedViewModel viewModel;

    public SavedFragment() {
        // Required empty public constructor
    }

    public static SavedFragment newInstance() {
        SavedFragment fragment = new SavedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View v =  inflater.inflate(R.layout.fragment_saved, container, false);
        mContext = container.getContext();

        ListView savedPostLists = (ListView) v.findViewById(R.id.savedPostslist);
        savedPostLists.setDivider(null);
        postList = new ArrayList<Post>();

        pa = new PostAdapter(container.getContext(),postList,false);

        savedPostLists.setAdapter(pa);

        //ViewModel
        viewModel = ViewModelProviders.of(this).get(SavedViewModel.class);
        viewModel.init(mContext);
        viewModel.getFavorites().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                postList.clear();
                postList.addAll(posts);
                pa.notifyDataSetChanged();
            }
        });

        return v;
    }
}
