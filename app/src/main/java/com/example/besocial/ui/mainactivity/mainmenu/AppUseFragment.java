package com.example.besocial.ui.mainactivity.mainmenu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.besocial.R;
import com.example.besocial.ui.mainactivity.MainActivity;


public class AppUseFragment extends Fragment {
    private ImageView logo;
    private int numOfClicksOnLogo;
    private NavController navController;
    public AppUseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_use, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= MainActivity.getNavController();
        numOfClicksOnLogo=0;
        logo=view.findViewById(R.id.app_use_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numOfClicksOnLogo++;
                if(numOfClicksOnLogo==3){
                    navController.navigate(R.id.action_appUseFragment_to_nav_about);
                }
            }
        });
    }
}
