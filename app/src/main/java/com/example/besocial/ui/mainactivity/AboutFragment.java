package com.example.besocial.ui.mainactivity;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.besocial.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    //private static AboutFragment aboutFragment;
    public AboutFragment() {
        // Required empty public constructor
    }

/*    public static AboutFragment getAboutFragment() {
        if(aboutFragment==null)
            aboutFragment=new AboutFragment();
        return aboutFragment;
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
