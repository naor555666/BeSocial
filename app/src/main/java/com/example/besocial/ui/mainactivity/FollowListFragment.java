package com.example.besocial.ui.mainactivity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.besocial.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowListFragment extends Fragment {


    public FollowListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow_list, container, false);
    }

}
