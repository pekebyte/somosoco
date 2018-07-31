package com.pekebyte.somosoco.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.pekebyte.somosoco.R;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    Boolean isChecked;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = container.getContext().getSharedPreferences("pekebyte.com.somosoco", MODE_PRIVATE);

        isChecked = sharedPreferences.getBoolean("notificationsEnabled",true);

        Switch notificationsEnabled = (Switch) v.findViewById(R.id.notificationsEnabled);

        notificationsEnabled.setChecked(isChecked);

        notificationsEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("notificationsEnabled",isChecked).apply();
            }
        });
        return v;
    }
}
