package com.example.besocial.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.besocial.MainActivity;
import com.example.besocial.R;
import com.google.android.gms.tasks.OnCompleteListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SocialCenterFragment extends Fragment implements View.OnClickListener {
    Button socialEventButton, bonusAreaButton, volunteerButton, getHelpButton;
    NavController navController;

    public SocialCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        socialEventButton = view.findViewById(R.id.socialEventButton);
        bonusAreaButton = view.findViewById(R.id.bonusAreaButton);
        volunteerButton = view.findViewById(R.id.volunteerButton);
        getHelpButton = view.findViewById(R.id.getHelpButton);
        setListeners();

        navController = MainActivity.getNavController();
    }

    private void setListeners() {
        socialEventButton.setOnClickListener(this);
        bonusAreaButton.setOnClickListener(this);
        volunteerButton.setOnClickListener(this);
        getHelpButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getHelpButton:
                navController.navigate(R.id.action_nav_social_center_to_createEventFragment);
                break;
            case R.id.socialEventButton:
                navController.navigate(R.id.action_nav_social_center_to_eventsListFragment);
                break;
            case R.id.volunteerButton:
                navController.navigate(R.id.action_nav_social_center_to_eventsListFragment);
                break;

            default:
                break;

        }
    }
}
