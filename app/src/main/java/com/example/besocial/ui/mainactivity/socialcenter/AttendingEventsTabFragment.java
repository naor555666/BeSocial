package com.example.besocial.ui.mainactivity.socialcenter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.besocial.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendingEventsTabFragment extends Fragment {

    private static final String TAG = "AttendingEventsTab";

    public AttendingEventsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attending_events_tab, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,TAG+"onStart");
    }
}
