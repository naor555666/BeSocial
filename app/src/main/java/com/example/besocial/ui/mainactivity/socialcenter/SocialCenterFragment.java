package com.example.besocial.ui.mainactivity.socialcenter;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.besocial.ui.mainactivity.MainActivity;
import com.example.besocial.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SocialCenterFragment extends Fragment implements View.OnClickListener {
    Button socialEventButton, bonusAreaButton, volunteerButton, getHelpButton;
    NavController navController;
    public static final String IS_HELP_EVENT="isHelpEvent";
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
        Bundle bundle = new Bundle();
        if (v.getId() == R.id.getHelpButton) {
            bundle.putBoolean(IS_HELP_EVENT, true);
            navController.navigate(R.id.action_nav_social_center_to_createEventFragment,bundle);
        }
        if (v.getId() == R.id.socialEventButton) {
            navController.navigate(R.id.action_nav_social_center_to_eventsListFragment,bundle);
        }
        if (v.getId() == R.id.volunteerButton) {
            bundle.putBoolean(IS_HELP_EVENT, true);
            navController.navigate(R.id.action_nav_social_center_to_eventsListFragment,bundle);
        }
    }
}
